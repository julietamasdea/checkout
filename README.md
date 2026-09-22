# Checkout Kata

Supermarket checkout that calculates the total price of scanned items, including promotions.

## Assumptions

- At most **one promotion per product**; promotions do not overlap on SKUs.
- **Buy N get 1 free** means: for every `N + 1` items, the customer pays for `N`.
- Prices are in **pence**.
- Pricing rules (product catalog + promotions) are passed when starting a checkout.

## How to run

```bash
mvn test
mvn spring-boot:run
```

Then open [http://localhost:8080/](http://localhost:8080/) (UI in `frontend/`).

Or call the API directly:

```bash
curl -s -X POST http://localhost:8080/checkout \
  -H 'Content-Type: application/json' \
  -d '{"items":["B","A","B"]}'
```

## Design

- Domain: `Promotion.discount(...)`, `CheckoutUseCase`, injectable `PricingRules`.
- API: stateless `POST /checkout` with the full SKU list → `{ "total" }`.
- UI: `frontend/` — add/remove SKUs, refresh total from the API.

## Trade-offs and known gaps

- **Validations** (positive unit prices, positive quantities, non-empty/known SKUs beyond the happy path) were not implemented due to time. I still consider them important for a real checkout: bad input should fail fast and clearly rather than produce a wrong total.
- **Full-list recalculation**: each request recomputes the total from the entire basket. An incremental model (update totals as items are added one by one) could be more efficient for large baskets or live scanning, but it needs more state and complexity (session/cart lifecycle, undo, promo recalculation). The brief asked for a total given items, not a long-lived cart, so the simpler stateless approach was preferred over over-engineering.
