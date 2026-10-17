package com.ecom.support;

import com.ecom.config.TestConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class TestBase {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void startBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(TestConfig.HEADLESS)
                        .setSlowMo(400));
    }

    @AfterAll
    static void stopBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void openContext() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 800)
                .setIgnoreHTTPSErrors(true));
        page = context.newPage();
        page.setDefaultTimeout(TestConfig.DEFAULT_TIMEOUT_MS);
        page.setDefaultNavigationTimeout(30_000);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    protected void open(String path) {
        String suffix = path.startsWith("/") ? path : "/" + path;
        page.navigate(TestConfig.BASE_URL + suffix);
        page.waitForLoadState();
        dismissNetlifyBadge();
    }

    protected void dismissNetlifyBadge() {
        page.evaluate("() => { const el = document.getElementById('nl-badge-frame'); if (el) el.remove(); }");
    }

    protected com.microsoft.playwright.Locator continueCheckoutButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(java.util.regex.Pattern.compile("Continue to (review|payment)")));
    }

    protected void waitForCheckoutForm() {
        try {
            continueCheckoutButton().waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(25_000));
        } catch (com.microsoft.playwright.TimeoutError error) {
            String text = page.locator("body").innerText();
            if (text.length() > 1200) {
                text = text.substring(0, 1200);
            }
            throw new AssertionError("Checkout form missing. url=" + page.url() + " text=" + text, error);
        }
    }

    protected void addProductToCart() {
        open("/products/" + TestConfig.PRODUCT_SLUG);
        page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(TestConfig.PRODUCT_NAME))
                .waitFor();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Cart \\(")))
                .waitFor();
    }

    /** Drawer Checkout is a React Router link, so the in-memory cart stays. */
    protected void goToCheckoutWithCart() {
        addProductToCart();
        dismissNetlifyBadge();
        page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your cart")).waitFor();
        page.getByText(TestConfig.PRODUCT_NAME).first().waitFor();
        page.locator("a[href='/checkout']").first().click();
        page.waitForURL("**/checkout", new Page.WaitForURLOptions().setTimeout(15_000));
        waitForCheckoutForm();
    }

    protected void registerCustomer(String name, String email, String password) {
        open("/register");
        page.getByLabel("Name").fill(name);
        page.getByLabel("Email").fill(email);
        page.getByLabel("Password").fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create account")).click();
        page.waitForURL("**/account");
    }

    protected void login(String email, String password) {
        open("/login");
        page.getByLabel("Email").fill(email);
        page.getByLabel("Password").fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in")).click();
    }

    protected void fillCheckoutAddress() {
        page.evaluate("""
                () => {
                  const setNative = (name, value) => {
                    const el = document.querySelector(`input[name='${name}']`);
                    if (!el) return;
                    const desc = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value');
                    desc.set.call(el, value);
                    el.dispatchEvent(new Event('input', { bubbles: true }));
                    el.dispatchEvent(new Event('change', { bubbles: true }));
                  };
                  setNative('line1', '100 Market Street');
                  setNative('city', 'San Francisco');
                  setNative('state', 'CA');
                  setNative('postalCode', '94105');
                  setNative('country', 'US');
                }
                """);
    }
}
