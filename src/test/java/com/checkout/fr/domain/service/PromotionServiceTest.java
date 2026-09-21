package com.checkout.fr.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.fr.domain.model.BuyNGetOneFreePromotion;
import com.checkout.fr.domain.model.MealDealPromotion;
import com.checkout.fr.domain.model.MultiPricedPromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PromotionServiceTest {

  private final PricingRules rules =
      new PricingRules(
          List.of(
              Product.builder().id("A").price(BigDecimal.valueOf(50)).build(),
              Product.builder().id("C").price(BigDecimal.valueOf(25)).build(),
              Product.builder().id("D").price(BigDecimal.valueOf(150)).build(),
              Product.builder().id("E").price(BigDecimal.valueOf(200)).build()),
          List.of(
              new MultiPricedPromotion("A", 3, BigDecimal.valueOf(130)),
              new BuyNGetOneFreePromotion("C", 3),
              new MealDealPromotion(Set.of("D", "E"), BigDecimal.valueOf(300))));

  @Test
  void multiPricedReturnsDiscountOnlyForCompleteGroups() {
    var service = new MultiPricedPromotionService();

    assertEquals(0, service.discount(Map.of("A", 2), rules).compareTo(BigDecimal.ZERO));
    assertEquals(0, service.discount(Map.of("A", 4), rules).compareTo(BigDecimal.valueOf(20)));
  }

  @Test
  void buyNGetOneFreeReturnsDiscountForFreeItems() {
    var service = new BuyNGetOneFreePromotionService();

    assertEquals(0, service.discount(Map.of("C", 3), rules).compareTo(BigDecimal.ZERO));
    assertEquals(0, service.discount(Map.of("C", 4), rules).compareTo(BigDecimal.valueOf(25)));
  }

  @Test
  void mealDealReturnsDiscountForEachCompleteDeal() {
    var service = new MealDealPromotionService();

    assertEquals(0, service.discount(Map.of("D", 1), rules).compareTo(BigDecimal.ZERO));
    assertEquals(
        0, service.discount(Map.of("D", 2, "E", 1), rules).compareTo(BigDecimal.valueOf(50)));
  }
}
