package com.ecom.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class HomePage {
    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public boolean showsAutumnCollection() {
        return page.getByText("Autumn collection").first().isVisible();
    }

    public boolean showsHeroHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions()
                .setName(java.util.regex.Pattern.compile("Quiet luxury"))).isVisible();
    }
}
