package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.MealDealPromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.PromotionType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/** Meal deal: buy a set of different items together for a special price (e.g. D + E for £3). */
public class MealDealPromotionService implements PromotionService {

  @Override
  public PromotionType getType() {
    return PromotionType.MEAL_DEAL;
  }

  @Override
  public BigDecimal discount(Map<String, Integer> quantities, PricingRules rules) {
    return rules.promotionsOfType(MealDealPromotion.class).stream()
        .map(promotion -> discountFor(promotion, quantities, rules))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal discountFor(
      MealDealPromotion promotion, Map<String, Integer> quantities, PricingRules rules) {

    return Optional.of(countCompleteDeals(promotion, quantities))
        .filter(deals -> deals > 0)
        .map(deals -> discountForDeals(promotion, rules, deals))
        .filter(discount -> discount.signum() > 0)
        .orElse(BigDecimal.ZERO);
  }

  private int countCompleteDeals(MealDealPromotion promotion, Map<String, Integer> quantities) {
    return promotion.getRequiredProductIds().stream()
        .mapToInt(sku -> quantities.getOrDefault(sku, 0))
        .min()
        .orElse(0);
  }

  private BigDecimal discountForDeals(
      MealDealPromotion promotion, PricingRules rules, int deals) {

    BigDecimal priceWithoutOffer =
        promotion.getRequiredProductIds().stream()
            .map(rules::requireProduct)
            .map(Product::getPrice)
            .map(price -> price.multiply(BigDecimal.valueOf(deals)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal priceWithOffer = promotion.getDealPrice().multiply(BigDecimal.valueOf(deals));
    return priceWithoutOffer.subtract(priceWithOffer);
  }
}
