package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 2 only: open the catalog and check that a product is listed.
 */
class CatalogLiveTest {

    @Test
    void productsPageListsItems() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/products");

            assertTrue(page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName("All products")).isVisible(),
                    "Products page should show the All products heading");
            assertTrue(page.getByText("Ceramic pour-over set").first().isVisible(),
                    "Catalog should list Ceramic pour-over set");

            browser.close();
        }
    }
}
