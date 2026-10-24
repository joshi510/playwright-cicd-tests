package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 7 only: register a new customer and land on the profile page.
 */
class RegisterLiveTest {

    @Test
    void registerCreatesAccount() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));
        String email = "user" + System.currentTimeMillis() + "@test.com";

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/register");

            page.getByLabel("Name").fill("Test Shopper");
            page.getByLabel("Email").fill(email);
            page.getByLabel("Password").fill("Password123!");
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Create account")).click();

            page.waitForURL("**/account");
            assertTrue(page.url().contains("/account"), "Register should open the account page");
            assertTrue(page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName("Profile")).isVisible(),
                    "Profile heading should be visible");
            assertTrue(page.getByText(email).first().isVisible(),
                    "Signed-in email should be visible");

            browser.close();
        }
    }
}
