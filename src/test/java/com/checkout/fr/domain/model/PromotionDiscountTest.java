package com.checkout.fr.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.checkout.fr.domain.model.promotion.BuyNGetOneFreePromotion;
import com.checkout.fr.domain.model.promotion.MealDealPromotion;
import com.checkout.fr.domain.model.promotion.MultiPricedPromotion;
import com.checkout.fr.domain.model.promotion.Promotion;
import org.junit.jupiter.api.Test;

class PromotionDiscountTest {

  private final PricingRules rules =
      new PricingRules(
          List.of(
              new Product("A", BigDecimal.valueOf(50)),
              new Product("C", BigDecimal.valueOf(25)),
              new Product("D", BigDecimal.valueOf(150)),
              new Product("E", BigDecimal.valueOf(200))),
          List.of(
              new MultiPricedPromotion("A", 3, BigDecimal.valueOf(130)),
              new BuyNGetOneFreePromotion("C", 3),
              new MealDealPromotion(Set.of("D", "E"), BigDecimal.valueOf(300))));

  @Test
  void multiPricedDiscountOnlyForCompleteGroups() {
    Promotion promo = rules.getPromotions().get(0);

    assertEquals(0, promo.discount(Map.of("A", 2), rules).compareTo(BigDecimal.ZERO));
    assertEquals(0, promo.discount(Map.of("A", 4), rules).compareTo(BigDecimal.valueOf(20)));
  }

  @Test
  void buyNGetOneFreeDiscountForFreeItems() {
    Promotion promo = rules.getPromotions().get(1);

    assertEquals(0, promo.discount(Map.of("C", 3), rules).compareTo(BigDecimal.ZERO));
    assertEquals(0, promo.discount(Map.of("C", 4), rules).compareTo(BigDecimal.valueOf(25)));
  }

  @Test
  void mealDealDiscountForCompleteDeals() {
    Promotion promo = rules.getPromotions().get(2);

    assertEquals(0, promo.discount(Map.of("D", 1), rules).compareTo(BigDecimal.ZERO));
    assertEquals(0, promo.discount(Map.of("D", 2, "E", 1), rules).compareTo(BigDecimal.valueOf(50)));
  }
}
