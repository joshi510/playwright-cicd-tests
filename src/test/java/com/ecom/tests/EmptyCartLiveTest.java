package com.ecom.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 6 only: a new browser with no items should see an empty cart.
 */
class EmptyCartLiveTest {

    @Test
    void emptyCartShowsMessage() {
        boolean headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "false"));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();
            page.navigate("https://ecomcicid.netlify.app/cart");

            assertTrue(page.getByText(Pattern.compile("cart is empty", Pattern.CASE_INSENSITIVE))
                            .isVisible(),
                    "Empty cart should say the cart is empty");

            browser.close();
        }
    }
}
