# Northline Live Test Cases

| | |
|---|---|
| **App** | https://ecomcicid.netlify.app |
| **Tool** | Java + Playwright + JUnit 5 |
| **Environment** | Live (Netlify), not local |

> **Note:** Netlify is a static site (no Express/Postgres). Cart, auth, and orders use browser storage. Admin pages open, but API metrics and lists stay empty. This is expected.

## Credentials

| Role | Email | Password |
|---|---|---|
| Admin (live) | `admin@example.com` | `Password123!` |
| Customer | Any unique email | `Password123!` |

Customer password rule: 8+ characters with upper, lower, number, and symbol.

---

## A. User Side

| ID | Title | Steps | Expected | Automated |
|---|---|---|---|---|
| U01 | Home loads | Open `/` | Title has **Northline**. Badge **Autumn collection**. Heading **Quiet luxury**. | Yes |
| U02 | Catalog lists products | Open `/products` | Heading **All products**. At least one product (Ceramic pour-over set). | Yes |
| U03 | Product detail | Open `/products/ceramic-pour-over-set` | Name, **Add to cart**, **Buy now**. | Yes |
| U04 | Add to cart | Detail → Add to cart | Header cart shows 1+ item. | Yes |
| U05 | Cart page | Add item → `/cart` | Heading **Cart**. Line item + **Checkout**. | Yes |
| U06 | Empty cart | `/cart` with no items | **Your cart is empty.** | Yes |
| U07 | Register | `/register` with unique email + strong password | `/account`, **Profile**, signed-in email. | Yes |
| U08 | Login | `/login` with registered user | `/account`, signed in. | Yes |
| U09 | Bad login | Register, then login with wrong password | Still on login. Sign-in failed / invalid password. | Yes |
| U10 | Guest account guard | New browser, open `/account` | Redirect to `/login`. | Yes |
| U11 | Checkout empty | New browser, open `/checkout` | **Your cart is empty** / Add something first. | Yes |
| U12 | Place order | Add item → checkout → address → Place order | Confirmation **Thank you** / **Order confirmed**. Order `ORD-LOCAL-…`. | Yes |
| U13 | Account orders | Place order while logged in → `/account/orders` | Orders page. Local order visible. | Yes |
| U14 | Wishlist | Login → product **Save** → `/account/wishlist` | Wishlist heading. Saved product visible. | Yes |
| U15 | Addresses | Login → `/account/addresses` → Add address → Save | Address appears on the list. | Yes |

> **Note:** Checkout has **no Stripe**. Flow is shipping → review → **Place order**.

---

## B. Admin Side (Live UI Only)

| ID | Title | Steps | Expected | Automated |
|---|---|---|---|---|
| A01 | Guest admin | New browser, open `/admin` | Redirect to `/login`. | Yes |
| A02 | Customer blocked | Customer login, open `/admin` | Redirect to `/` (home). Not Admin layout. | Yes |
| A03 | Admin login | Login with `admin@example.com` / `Password123!` | `/admin`. **Northline Admin** sidebar. | Yes |
| A04 | Dashboard | After admin login | Sidebar visible. Metrics may show **Could not load dashboard metrics** (counts as pass). | Yes |
| A05 | Admin products | Open `/admin/products` | Heading **Products**. Empty/error list is OK. | Yes |
| A06 | Admin orders | Open `/admin/orders` | Heading **Orders**. | Yes |
| A07 | Admin customers | Open `/admin/customers` | Heading **Customers**. | Yes |
| A08 | Admin categories | Open `/admin/categories` | Heading **Categories**. | Yes |
| A09 | Admin settings | Open `/admin/settings` | Heading **Settings**. | Yes |
| A10 | Admin nav | Click Dashboard, Products, Orders, Customers, Categories, Settings | Each URL stays under `/admin`. Sidebar stays visible. | Yes |

> **Note:** Do **not** fail admin tests because tables are empty. There is no live API.

---

## C. Out of Scope on Live

- Real Stripe card payment
- Postgres users / merge cart on login
- Admin create product, change order status, refund

These need a local API.

---

## D. How to Run (Eclipse)

1. Import `playwright-cicd-tests` as an existing Maven project.
2. Right-click `com.ecom.tests` → **Run As** → **JUnit Test** (JUnit 5).
3. *(Optional)* Set env `HEADLESS=false` to see the browser.
4. CLI: run `mvn test` from the project folder.



Pachi order:

Order	File	Shu
1
EcomSmokeTest
Site load
2
CartAndCheckoutTest
Add to cart + Place order
3
UserLiveTest
U01–U15 (lambo)
4
AdminLiveTest
A01–A10