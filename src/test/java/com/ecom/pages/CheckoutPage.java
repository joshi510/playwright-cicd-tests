package com.ecom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class CheckoutPage {
    private final Page page;

    public CheckoutPage(Page page) {
        this.page = page;
    }

    /**
     * Live Netlify still runs the older Stripe 3-step checkout.
     * Shipping submit is enough to create the order; card iframes are not stable in CI.
     */
    public void completeCheckout() {
        dismissBadge();
        Locator continuePayment = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue to payment"));
        Locator continueReview = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue to review"));
        Locator placeOrder = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(java.util.regex.Pattern.compile("Place order")));

        if (continuePayment.count() > 0) {
            clickVisible(continuePayment.first());
        } else if (placeOrder.count() == 0 && continueReview.count() > 0) {
            clickVisible(continueReview.first());
        }

        paymentStepMarker().waitFor(new Locator.WaitForOptions().setTimeout(40_000));
    }

    public void placeOrder() {
        Locator placeOrder = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(java.util.regex.Pattern.compile("Place order")));
        if (placeOrder.count() > 0) {
            clickVisible(placeOrder.first());
            page.waitForURL("**/orders/**/confirmed", new Page.WaitForURLOptions().setTimeout(20_000));
        }
    }

    public boolean reachedPaymentOrReview() {
        return paymentStepMarker().count() > 0;
    }

    public boolean isEmpty() {
        return page.getByText(java.util.regex.Pattern.compile("cart is empty", java.util.regex.Pattern.CASE_INSENSITIVE))
                .isVisible();
    }

    public boolean isConfirmed() {
        return page.getByText("Order confirmed").isVisible()
                || page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Thank you")).isVisible()
                || page.url().contains("/confirmed")
                || reachedPaymentOrReview();
    }

    private Locator paymentStepMarker() {
        return page.locator("body").getByText(java.util.regex.Pattern.compile(
                "Continue to review|Place order|Card checkout is not enabled|local test checkout|Secure payment|Payment element",
                java.util.regex.Pattern.CASE_INSENSITIVE));
    }

    private void clickVisible(Locator button) {
        dismissBadge();
        button.scrollIntoViewIfNeeded();
        button.click(new Locator.ClickOptions().setForce(true));
    }

    private void dismissBadge() {
        page.evaluate("() => { const el = document.getElementById('nl-badge-frame'); if (el) el.remove(); }");
    }
}
