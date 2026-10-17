package com.ecom.config;

public final class TestConfig {

    public static final String BASE_URL =
            getenv("BASE_URL", "https://ecomcicid.netlify.app");

    public static final String PRODUCT_SLUG = "ceramic-pour-over-set";
    public static final String PRODUCT_NAME = "Ceramic pour-over set";

    public static final String CUSTOMER_PASSWORD = "Password123!";
    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String ADMIN_PASSWORD = "Password123!";

    public static final boolean HEADLESS = "true".equalsIgnoreCase(getenv("HEADLESS", "false"));

    public static final int DEFAULT_TIMEOUT_MS = 20_000;

    private TestConfig() {}

    private static String getenv(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public static String uniqueEmail(String prefix) {
        return prefix + System.currentTimeMillis() + "@test.com";
    }
}
