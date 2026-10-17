package com.ecom.tests;

import com.ecom.config.TestConfig;
import com.ecom.support.TestBase;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminLiveTest extends TestBase {

    private void loginAsAdmin() {
        login(TestConfig.ADMIN_EMAIL, TestConfig.ADMIN_PASSWORD);
        page.waitForURL(url -> url.contains("/admin") || url.contains("/account") || url.endsWith("/"));
        if (!page.url().contains("/admin")) {
            open("/admin");
        }
        page.getByText("Northline Admin").waitFor();
    }

    @Test
    @DisplayName("A01 Guest is sent to login from /admin")
    void a01GuestAdmin() {
        open("/admin");
        page.waitForURL("**/login");
        assertTrue(page.url().contains("/login"));
    }

    @Test
    @DisplayName("A02 Customer cannot open admin")
    void a02CustomerBlocked() {
        String email = TestConfig.uniqueEmail("cust");
        registerCustomer("Normal Customer", email, TestConfig.CUSTOMER_PASSWORD);
        open("/admin");
        page.waitForTimeout(1500);
        assertFalse(page.url().contains("/admin"), "Customer must be redirected away from /admin");
        assertFalse(page.getByText("Northline Admin").isVisible(), "Customer must not see admin chrome");
    }

    @Test
    @DisplayName("A03 Admin login opens /admin")
    void a03AdminLogin() {
        loginAsAdmin();
        assertTrue(page.url().contains("/admin"));
        assertTrue(page.getByText("Northline Admin").isVisible());
    }

    @Test
    @DisplayName("A04 Dashboard UI (empty metrics OK on live)")
    void a04Dashboard() {
        loginAsAdmin();
        open("/admin");
        assertTrue(page.getByText("Northline Admin").isVisible());
        boolean heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Dashboard")).count() > 0;
        boolean apiDown = page.getByText("Could not load dashboard metrics").count() > 0;
        assertTrue(heading || apiDown, "Dashboard heading or live API-empty message");
    }

    @Test
    @DisplayName("A05 Admin products page")
    void a05Products() {
        loginAsAdmin();
        open("/admin/products");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Products")).isVisible());
    }

    @Test
    @DisplayName("A06 Admin orders page")
    void a06Orders() {
        loginAsAdmin();
        open("/admin/orders");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Orders")).isVisible());
    }

    @Test
    @DisplayName("A07 Admin customers page")
    void a07Customers() {
        loginAsAdmin();
        open("/admin/customers");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Customers")).isVisible());
    }

    @Test
    @DisplayName("A08 Admin categories page")
    void a08Categories() {
        loginAsAdmin();
        open("/admin/categories");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Categories")).isVisible());
    }

    @Test
    @DisplayName("A09 Admin settings page")
    void a09Settings() {
        loginAsAdmin();
        open("/admin/settings");
        assertTrue(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Settings")).isVisible());
    }

    @Test
    @DisplayName("A10 Admin navigation stays in /admin")
    void a10Nav() {
        loginAsAdmin();
        String[][] links = {
                {"Dashboard", "/admin"},
                {"Products", "/admin/products"},
                {"Orders", "/admin/orders"},
                {"Customers", "/admin/customers"},
                {"Categories", "/admin/categories"},
                {"Settings", "/admin/settings"}
        };
        for (String[] link : links) {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(link[0])).first().click();
            page.waitForTimeout(400);
            assertTrue(page.url().contains("/admin"), link[0] + " should stay in admin");
            assertTrue(page.getByText("Northline Admin").isVisible());
        }
    }
}
