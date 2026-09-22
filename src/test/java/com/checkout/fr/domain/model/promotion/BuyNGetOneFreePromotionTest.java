package com.checkout.fr.domain.model.promotion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BuyNGetOneFreePromotionTest {

  private final BuyNGetOneFreePromotion promo = new BuyNGetOneFreePromotion("C", 3);
  private final PricingRules rules =
      new PricingRules(
          List.of(new Product("C", BigDecimal.valueOf(25))), List.of(promo));

  @Test
  @DisplayName("no free item yet when quantity < N+1")
  void noFreeItem() {
    assertDiscount(Map.of("C", 0), 0);
    assertDiscount(Map.of("C", 3), 0);
  }

  @Test
  @DisplayName("4 C → 1 free → discount 25")
  void oneFreeItem() {
    assertDiscount(Map.of("C", 4), 25);
  }

  @Test
  @DisplayName("5 C → still 1 free → discount 25")
  void remainderDoesNotAddExtraFree() {
    assertDiscount(Map.of("C", 5), 25);
  }

  @Test
  @DisplayName("8 C → 2 free → discount 50")
  void twoFreeItems() {
    assertDiscount(Map.of("C", 8), 50);
  }

  private void assertDiscount(Map<String, Integer> quantities, int expectedPence) {
    assertEquals(
        0,
        promo.discount(quantities, rules).compareTo(BigDecimal.valueOf(expectedPence)),
        () -> "expected " + expectedPence + " but was " + promo.discount(quantities, rules));
  }
}
