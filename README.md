# Northline Playwright + JUnit (live)

Tests hit **https://ecomcicid.netlify.app** only. Case list: [TEST_CASES.md](TEST_CASES.md).

## Eclipse

1. File → Open Projects from File System → this folder  
2. Maven → Update Project  
3. `src/test/java/com/ecom/tests` → Run As → JUnit Test (JUnit 5)

Optional Run Configuration environment:

- `HEADLESS=false` — see the browser  
- `BASE_URL=https://ecomcicid.netlify.app`

## Maven

```text
mvn test
```

First run downloads Chromium.

## Classes

| Class | Cases |
|---|---|
| `EcomSmokeTest` | Site load |
| `UserLiveTest` | U01–U15 |
| `AdminLiveTest` | A01–A10 |
| `CartAndCheckoutTest` | Add to cart + Place order |

Admin dashboard tables can be empty on live. That is a pass, not a fail.
