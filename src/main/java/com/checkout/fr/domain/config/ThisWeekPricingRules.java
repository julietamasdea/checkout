package com.checkout.fr.domain.config;

import com.checkout.fr.domain.model.BuyNGetOneFreePromotion;
import com.checkout.fr.domain.model.MealDealPromotion;
import com.checkout.fr.domain.model.MultiPricedPromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/** This week's pricing rules from the kata statement. */
public final class ThisWeekPricingRules {

  private ThisWeekPricingRules() {}

  public static PricingRules create() {
    return new PricingRules(
        List.of(
            Product.builder().id("A").price(BigDecimal.valueOf(50)).build(),
            Product.builder().id("B").price(BigDecimal.valueOf(75)).build(),
            Product.builder().id("C").price(BigDecimal.valueOf(25)).build(),
            Product.builder().id("D").price(BigDecimal.valueOf(150)).build(),
            Product.builder().id("E").price(BigDecimal.valueOf(200)).build()),
        List.of(
            new MultiPricedPromotion("A", 3, BigDecimal.valueOf(130)),
            new MultiPricedPromotion("B", 2, BigDecimal.valueOf(125)),
            new BuyNGetOneFreePromotion("C", 3),
            new MealDealPromotion(Set.of("D", "E"), BigDecimal.valueOf(300))));
  }
}
