package com.ecom.tests;

import com.ecom.config.TestConfig;
import com.ecom.pages.CheckoutPage;
import com.ecom.support.TestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CartAndCheckoutTest extends TestBase {

    @Test
    @DisplayName("E2E: add product then place order on live")
    void testAddToCartAndCheckout() {
        goToCheckoutWithCart();
        assertTrue(page.getByText(TestConfig.PRODUCT_NAME).first().isVisible());
        fillCheckoutAddress();
        CheckoutPage checkout = new CheckoutPage(page);
        checkout.completeCheckout();
        assertTrue(checkout.isConfirmed());
    }
}
