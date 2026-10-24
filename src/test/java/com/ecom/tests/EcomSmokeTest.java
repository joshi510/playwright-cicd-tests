package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 1 only: open the live storefront and check the page title.
 */
class EcomSmokeTest {

    @Test
    void homePageLoads() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/");

            String title = page.title();
            System.out.println("Page title: " + title);
            assertTrue(title.toLowerCase().contains("northline"),
                    "Expected the live title to contain Northline, got: " + title);

            browser.close();
        }
    }
}
