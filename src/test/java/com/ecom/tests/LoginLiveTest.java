package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 8 only: register a customer, sign out, then sign in again.
 */
class LoginLiveTest {

    @Test
    void loginWithRegisteredCustomer() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));
        String email = "login" + System.currentTimeMillis() + "@test.com";

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/register");

            page.getByLabel("Name").fill("Login User");
            page.getByLabel("Email").fill(email);
            page.getByLabel("Password").fill("Password123!");
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Create account")).click();
            page.waitForURL("**/account");

            page.evaluate("() => localStorage.removeItem('ecom-auth')");
            page.navigate("https://ecomcicid.netlify.app/login");
            page.getByLabel("Email").fill(email);
            page.getByLabel("Password").fill("Password123!");
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Sign in")).click();

            page.waitForURL("**/account");
            assertTrue(page.url().contains("/account"), "Login should open the account page");
            assertTrue(page.getByText(email).first().isVisible(),
                    "Signed-in email should be visible");

            browser.close();
        }
    }
}
