package com.checkout.fr.domain.model.promotion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MealDealPromotionTest {

  private final MealDealPromotion promo =
      new MealDealPromotion(Set.of("D", "E"), BigDecimal.valueOf(300));
  private final PricingRules rules =
      new PricingRules(
          List.of(
              new Product("D", BigDecimal.valueOf(150)),
              new Product("E", BigDecimal.valueOf(200))),
          List.of(promo));

  @Test
  @DisplayName("no deal without both products")
  void incompleteDeal() {
    assertDiscount(Map.of("D", 1), 0);
    assertDiscount(Map.of("E", 2), 0);
    assertDiscount(Map.of(), 0);
  }

  @Test
  @DisplayName("one D+E saves 50 (350 - 300)")
  void oneDeal() {
    assertDiscount(Map.of("D", 1, "E", 1), 50);
  }

  @Test
  @DisplayName("extra D does not increase discount beyond one deal")
  void leftoverDoesNotAddDeal() {
    assertDiscount(Map.of("D", 2, "E", 1), 50);
  }

  @Test
  @DisplayName("two full deals save 100")
  void twoDeals() {
    assertDiscount(Map.of("D", 2, "E", 2), 100);
  }

  private void assertDiscount(Map<String, Integer> quantities, int expectedPence) {
    assertEquals(
        0,
        promo.discount(quantities, rules).compareTo(BigDecimal.valueOf(expectedPence)),
        () -> "expected " + expectedPence + " but was " + promo.discount(quantities, rules));
  }
}
