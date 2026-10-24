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
 * Step 4 only: add one product and check the header cart count.
 */
class AddToCartLiveTest {

    @Test
    void addToCartUpdatesHeaderCount() {
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

            assertTrue(page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(Pattern.compile("Cart \\(1\\)"))).isVisible(),
                    "Header cart should show 1 item");

            browser.close();
        }
    }
}
