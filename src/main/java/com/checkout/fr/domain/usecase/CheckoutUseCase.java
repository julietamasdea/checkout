package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.exception.InvalidProductException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Checkout for one transaction. Pricing rules are passed at construction time.
 *
 * <p>Assumption: each product has at most one promotion; promotions do not overlap on SKUs.
 */
public class CheckoutUseCase {

  private final PricingRules pricingRules;

  public CheckoutUseCase(PricingRules pricingRules) {
    this.pricingRules = pricingRules;
  }

  /** Calculates the total from a quantity map (sku → count). */
  public BigDecimal process(Map<String, Integer> quantities) {
    quantities.keySet().forEach(this::requireKnown);
    return recalculate(quantities);
  }

  private void requireKnown(String productId) {
    if (!pricingRules.contains(productId)) {
      throw new InvalidProductException("Unknown product: " + productId);
    }
  }

  private BigDecimal recalculate(Map<String, Integer> scannedItems) {
    return unitTotal(scannedItems).subtract(totalDiscount(scannedItems));
  }

  private BigDecimal unitTotal(Map<String, Integer> scannedItems) {
    return scannedItems.entrySet().stream()
        .map(
            entry ->
                pricingRules
                    .requireProduct(entry.getKey())
                    .price()
                    .multiply(BigDecimal.valueOf(entry.getValue())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal totalDiscount(Map<String, Integer> scannedItems) {
    return pricingRules.getPromotions().stream()
        .map(promotion -> promotion.discount(scannedItems, pricingRules))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
