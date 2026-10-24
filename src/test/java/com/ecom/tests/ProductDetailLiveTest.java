package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 3 only: open one product and check the cart actions.
 */
class ProductDetailLiveTest {

    @Test
    void productDetailShowsCartActions() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/products/ceramic-pour-over-set");

            assertTrue(page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName("Ceramic pour-over set")).isVisible(),
                    "Product name should be visible");
            assertTrue(page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Add to cart")).isVisible(),
                    "Add to cart should be visible");
            assertTrue(page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Buy now")).isVisible(),
                    "Buy now should be visible");

            browser.close();
        }
    }
}
