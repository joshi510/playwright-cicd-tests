package com.ecom.tests;

import com.ecom.pages.HomePage;
import com.ecom.support.TestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EcomSmokeTest extends TestBase {

    @Test
    @DisplayName("Smoke: live storefront loads")
    void testWebsiteLoads() {
        open("/");
        HomePage home = new HomePage(page);
        assertTrue(page.title().length() > 0, "Title should not be empty");
        assertTrue(page.locator("body").isVisible(), "Body should be visible");
        assertTrue(home.showsAutumnCollection() || page.title().toLowerCase().contains("northline"));
    }
}
