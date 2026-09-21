package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.service.PromotionService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Checkout for one transaction. Pricing rules are passed at construction time.
 *
 * <p>Assumption: each product has at most one promotion; promotions do not overlap on SKUs.
 */
@Getter
public class CheckoutUseCase {

  private final PricingRules pricingRules;
  private final List<PromotionService> promotionServices;
  private final Map<String, Integer> scannedItems = new HashMap<>();
  private BigDecimal finalPrice = BigDecimal.ZERO;

  public CheckoutUseCase(PricingRules pricingRules, List<PromotionService> promotionServices) {
    this.pricingRules = pricingRules;
    this.promotionServices = List.copyOf(promotionServices);
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
                    .getPrice()
                    .multiply(BigDecimal.valueOf(entry.getValue())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal totalDiscount() {
    return promotionServices.stream()
        .map(service -> service.discount(scannedItems, pricingRules))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
