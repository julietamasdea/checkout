package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.model.PricingRules;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Checkout for one transaction. Pricing rules are passed at construction time.
 *
 * <p>Assumption: each product has at most one promotion; promotions do not overlap on SKUs.
 */
public class CheckoutUseCase {

  private final PricingRules pricingRules;
  private final Map<String, Integer> scannedItems = new HashMap<>();
  private BigDecimal finalPrice = BigDecimal.ZERO;

  public CheckoutUseCase(PricingRules pricingRules) {
    this.pricingRules = pricingRules;
  }

  public void addProduct(String productId) {
    if (!pricingRules.contains(productId)) {
      throw new IllegalArgumentException("Unknown product: " + productId);
    }
    scannedItems.merge(productId, 1, Integer::sum);
    finalPrice = recalculate();
  }

  public BigDecimal total() {
    return finalPrice;
  }

  private BigDecimal recalculate() {
    return unitTotal().subtract(totalDiscount());
  }

  private BigDecimal unitTotal() {
    return scannedItems.entrySet().stream()
        .map(
            entry ->
                pricingRules
                    .requireProduct(entry.getKey())
                    .price()
                    .multiply(BigDecimal.valueOf(entry.getValue())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal totalDiscount() {
    return pricingRules.getPromotions().stream()
        .map(promotion -> promotion.discount(scannedItems, pricingRules))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
