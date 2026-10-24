package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 9 only: a wrong password must stay on the login page.
 */
class BadLoginLiveTest {

    @Test
    void wrongPasswordStaysOnLogin() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));
        String email = "bad" + System.currentTimeMillis() + "@test.com";

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/register");

            page.getByLabel("Name").fill("Bad Login");
            page.getByLabel("Email").fill(email);
            page.getByLabel("Password").fill("Password123!");
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Create account")).click();
            page.waitForURL("**/account");

            page.evaluate("() => localStorage.removeItem('ecom-auth')");
            page.navigate("https://ecomcicid.netlify.app/login");
            page.getByLabel("Email").fill(email);
            page.getByLabel("Password").fill("Wrongpass1!");
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Sign in")).click();

            page.waitForTimeout(1000);
            assertTrue(page.url().contains("/login"), "Wrong password should stay on login");
            assertFalse(page.url().contains("/account"), "Wrong password should not open the account");
            assertTrue(page.getByText(Pattern.compile("invalid|failed|incorrect", Pattern.CASE_INSENSITIVE))
                            .first().isVisible(),
                    "Login page should show a sign-in failure");

            browser.close();
        }
    }
}
