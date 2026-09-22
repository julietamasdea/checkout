package com.checkout.fr.domain.config;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.promotion.BuyNGetOneFreePromotion;
import com.checkout.fr.domain.model.promotion.MealDealPromotion;
import com.checkout.fr.domain.model.promotion.MultiPricedPromotion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/** This week's pricing rules from the kata statement. */
public final class ThisWeekPricingRules {

  private ThisWeekPricingRules() {}

  public static PricingRules create() {
    return new PricingRules(
        List.of(
            new Product("A", BigDecimal.valueOf(50)),
            new Product("B", BigDecimal.valueOf(75)),
            new Product("C", BigDecimal.valueOf(25)),
            new Product("D", BigDecimal.valueOf(150)),
            new Product("E", BigDecimal.valueOf(200))),
        List.of(
            new MultiPricedPromotion("A", 3, BigDecimal.valueOf(130)),
            new MultiPricedPromotion("B", 2, BigDecimal.valueOf(125)),
            new BuyNGetOneFreePromotion("C", 3),
            new MealDealPromotion(Set.of("D", "E"), BigDecimal.valueOf(300))));
  }
}
