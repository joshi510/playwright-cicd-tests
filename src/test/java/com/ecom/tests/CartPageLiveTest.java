package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 5 only: add one product, open the cart page, and check the line item.
 */
class CartPageLiveTest {

    @Test
    void cartPageShowsAddedProduct() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/products/ceramic-pour-over-set");

            page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName("Ceramic pour-over set")).waitFor();
            page.evaluate("() => { const el = document.getElementById('nl-badge-frame'); if (el) el.remove(); }");
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Add to cart")).click();
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(Pattern.compile("Cart \\(1\\)"))).waitFor();

            page.navigate("https://ecomcicid.netlify.app/cart");

            assertTrue(page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName("Cart")).isVisible(),
                    "Cart page should show the Cart heading");
            assertTrue(page.getByText("Ceramic pour-over set").first().isVisible(),
                    "Cart should list Ceramic pour-over set");
            assertTrue(page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Checkout")).isVisible(),
                    "Checkout button should be visible");

            browser.close();
        }
    }
}
