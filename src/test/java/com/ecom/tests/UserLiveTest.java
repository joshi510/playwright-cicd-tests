package com.ecom.tests;

import com.ecom.config.TestConfig;
import com.ecom.pages.CheckoutPage;
import com.ecom.pages.HomePage;
import com.ecom.support.TestBase;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserLiveTest extends TestBase {

    @Test
    @DisplayName("U01 Home loads with autumn collection")
    void u01HomeLoads() {
        open("/");
        HomePage home = new HomePage(page);
        assertTrue(page.title().toLowerCase().contains("northline"), "Title should mention Northline");
        assertTrue(home.showsAutumnCollection(), "Autumn collection badge");
        assertTrue(home.showsHeroHeading(), "Hero heading");
    }

    @Test
    @DisplayName("U02 Catalog lists products")
    void u02Catalog() {
        open("/products");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("All products")).isVisible());
        assertTrue(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                .setName(java.util.regex.Pattern.compile(TestConfig.PRODUCT_NAME))).first().isVisible());
    }

    @Test
    @DisplayName("U03 Product detail shows cart actions")
    void u03ProductDetail() {
        open("/products/" + TestConfig.PRODUCT_SLUG);
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions()
                .setName(TestConfig.PRODUCT_NAME)).isVisible());
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart")).isVisible());
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Buy now")).isVisible());
    }

    @Test
    @DisplayName("U04 Add to cart updates header")
    void u04AddToCart() {
        addProductToCart();
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(java.util.regex.Pattern.compile("Cart \\("))).isVisible());
    }

    @Test
    @DisplayName("U05 Cart page shows the line item")
    void u05CartPage() {
        addProductToCart();
        open("/cart");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Cart")).isVisible());
        assertTrue(page.getByText(TestConfig.PRODUCT_NAME).first().isVisible());
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Checkout")).isVisible());
    }

    @Test
    @DisplayName("U06 Empty cart message")
    void u06EmptyCart() {
        open("/cart");
        assertTrue(page.getByText(java.util.regex.Pattern.compile("cart is empty", java.util.regex.Pattern.CASE_INSENSITIVE))
                .isVisible());
    }

    @Test
    @DisplayName("U07 Register creates a local account")
    void u07Register() {
        String email = TestConfig.uniqueEmail("user");
        registerCustomer("Test Shopper", email, TestConfig.CUSTOMER_PASSWORD);
        assertTrue(page.url().contains("/account"));
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Profile")).isVisible());
        assertTrue(page.getByText(email).first().isVisible());
    }

    @Test
    @DisplayName("U08 Login with registered customer")
    void u08Login() {
        String email = TestConfig.uniqueEmail("login");
        registerCustomer("Login User", email, TestConfig.CUSTOMER_PASSWORD);
        page.evaluate("() => localStorage.removeItem('ecom-auth')");
        login(email, TestConfig.CUSTOMER_PASSWORD);
        page.waitForURL("**/account");
        assertTrue(page.getByText(email).first().isVisible());
    }

    @Test
    @DisplayName("U09 Wrong password stays on login")
    void u09BadLogin() {
        String email = TestConfig.uniqueEmail("bad");
        registerCustomer("Bad Login", email, TestConfig.CUSTOMER_PASSWORD);
        page.evaluate("() => localStorage.removeItem('ecom-auth')");
        login(email, "Wrongpass1!");
        page.waitForTimeout(1000);
        assertTrue(page.url().contains("/login"), "Should remain on login");
        assertFalse(page.url().contains("/account"));
    }

    @Test
    @DisplayName("U10 Guest cannot open account")
    void u10GuestAccount() {
        open("/account");
        page.waitForURL("**/login");
        assertTrue(page.url().contains("/login"));
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome back")).isVisible());
    }

    @Test
    @DisplayName("U11 Checkout empty cart")
    void u11CheckoutEmpty() {
        open("/checkout");
        assertTrue(new CheckoutPage(page).isEmpty());
    }

    @Test
    @DisplayName("U12 Place order on live checkout")
    void u12PlaceOrder() {
        registerCustomer("Checkout User", TestConfig.uniqueEmail("chk"), TestConfig.CUSTOMER_PASSWORD);
        goToCheckoutWithCart();
        fillCheckoutAddress();
        CheckoutPage checkout = new CheckoutPage(page);
        checkout.completeCheckout();
        assertTrue(checkout.reachedPaymentOrReview(), "Should reach payment/review after shipping");
        checkout.placeOrder();
    }

    @Test
    @DisplayName("U13 Account orders after checkout")
    void u13AccountOrders() {
        String email = TestConfig.uniqueEmail("orders");
        registerCustomer("Order User", email, TestConfig.CUSTOMER_PASSWORD);
        goToCheckoutWithCart();
        fillCheckoutAddress();
        CheckoutPage checkout = new CheckoutPage(page);
        checkout.completeCheckout();
        open("/account/orders");
        page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Orders")).waitFor();
        assertTrue(
                page.getByText(java.util.regex.Pattern.compile("ORD-")).first().isVisible()
                        || page.getByText(TestConfig.PRODUCT_NAME).first().isVisible(),
                "Saved local order should appear on Orders");
    }

    @Test
    @DisplayName("U14 Save product to wishlist")
    void u14Wishlist() {
        String email = TestConfig.uniqueEmail("wish");
        registerCustomer("Wish User", email, TestConfig.CUSTOMER_PASSWORD);
        open("/products/" + TestConfig.PRODUCT_SLUG);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save")).click();
        open("/account/wishlist");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist")).isVisible());
        assertTrue(page.getByText(TestConfig.PRODUCT_NAME).first().isVisible());
    }

    @Test
    @DisplayName("U15 Add a shipping address")
    void u15Addresses() {
        String email = TestConfig.uniqueEmail("addr");
        registerCustomer("Addr User", email, TestConfig.CUSTOMER_PASSWORD);
        open("/account/addresses");
        page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Addresses")).waitFor();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add address"))
                .click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
        com.microsoft.playwright.Locator dialog = page.getByRole(AriaRole.DIALOG);
        dialog.waitFor();
        dialog.getByLabel("Address", new com.microsoft.playwright.Locator.GetByLabelOptions().setExact(true))
                .fill("200 Market Street");
        dialog.getByLabel("City", new com.microsoft.playwright.Locator.GetByLabelOptions().setExact(true)).fill("Austin");
        dialog.getByLabel("Postal code", new com.microsoft.playwright.Locator.GetByLabelOptions().setExact(true))
                .fill("78701");
        dialog.getByLabel("Country", new com.microsoft.playwright.Locator.GetByLabelOptions().setExact(true)).fill("US");
        dialog.getByRole(AriaRole.BUTTON, new com.microsoft.playwright.Locator.GetByRoleOptions().setName("Save address"))
                .click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
        page.getByText("200 Market Street").waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(25_000));
        assertTrue(page.getByText("Austin").first().isVisible());
    }
}
