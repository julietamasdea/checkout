package com.checkout.fr.domain.model.promotion;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.PromotionType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Meal deal: buy a set of different items together for a special price (e.g. D + E for £3). */
public class MealDealPromotion extends Promotion {

  private final Set<String> requiredProductIds;
  private final BigDecimal dealPrice;

  public MealDealPromotion(Set<String> requiredProductIds, BigDecimal dealPrice) {
    super(PromotionType.MEAL_DEAL);
    this.requiredProductIds = Set.copyOf(requiredProductIds);
    this.dealPrice = dealPrice;
  }

  public Set<String> getRequiredProductIds() {
    return requiredProductIds;
  }

  public BigDecimal getDealPrice() {
    return dealPrice;
  }

  @Override
  public BigDecimal discount(Map<String, Integer> quantities, PricingRules rules) {
    return Optional.of(countCompleteDeals(quantities))
        .filter(deals -> deals > 0)
        .map(deals -> discountForDeals(rules, deals))
        .filter(discount -> discount.signum() > 0)
        .orElse(BigDecimal.ZERO);
  }

  private int countCompleteDeals(Map<String, Integer> quantities) {
    return requiredProductIds.stream()
        .mapToInt(sku -> quantities.getOrDefault(sku, 0))
        .min()
        .orElse(0);
  }

  private BigDecimal discountForDeals(PricingRules rules, int deals) {
    BigDecimal priceWithoutOffer =
        requiredProductIds.stream()
            .map(rules::requireProduct)
            .map(Product::price)
            .map(price -> price.multiply(BigDecimal.valueOf(deals)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal priceWithOffer = dealPrice.multiply(BigDecimal.valueOf(deals));
    return priceWithoutOffer.subtract(priceWithOffer);
  }
}
