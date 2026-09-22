package com.checkout.fr.domain.model.promotion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultiPricedPromotionTest {

  private final MultiPricedPromotion promo =
      new MultiPricedPromotion("A", 3, BigDecimal.valueOf(130));
  private final PricingRules rules =
      new PricingRules(
          List.of(new Product("A", BigDecimal.valueOf(50))), List.of(promo));

  @Test
  @DisplayName("no discount when there are no complete groups")
  void noCompleteGroup() {
    assertDiscount(Map.of("A", 0), 0);
    assertDiscount(Map.of("A", 2), 0);
  }

  @Test
  @DisplayName("one group of 3 A saves 20 (150 - 130)")
  void oneGroup() {
    assertDiscount(Map.of("A", 3), 20);
  }

  @Test
  @DisplayName("leftover units do not get multiprice discount")
  void leftoverIgnoredInDiscount() {
    // 4 A → only 3 in the offer → discount still 20
    assertDiscount(Map.of("A", 4), 20);
  }

  @Test
  @DisplayName("two groups save 40")
  void twoGroups() {
    assertDiscount(Map.of("A", 6), 40);
  }

  @Test
  @DisplayName("other SKUs in the basket do not affect this promo")
  void ignoresOtherSkus() {
    assertDiscount(Map.of("A", 3, "B", 10), 20);
  }

  private void assertDiscount(Map<String, Integer> quantities, int expectedPence) {
    assertEquals(
        0,
        promo.discount(quantities, rules).compareTo(BigDecimal.valueOf(expectedPence)),
        () -> "expected " + expectedPence + " but was " + promo.discount(quantities, rules));
  }
}
