package com.checkout.fr.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.checkout.fr.domain.config.ThisWeekPricingRules;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.exception.InvalidProductException;
import com.checkout.fr.domain.model.promotion.MultiPricedPromotion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckoutUseCaseTest {

  private CheckoutUseCase checkout;

  @BeforeEach
  void setUp() {
    checkout = CheckoutFactory.start(ThisWeekPricingRules.create());
  }

  @Test
  @DisplayName("empty basket costs 0")
  void emptyCheckoutIsZero() {
    assertProcess(Map.of(), 0);
  }

  @Test
  @DisplayName("items without a full offer use unit price")
  void singleItemsUseUnitPrice() {
    assertProcess(Map.of("A", 1, "C", 1), 75);
  }

  @Test
  @DisplayName("unknown SKU is rejected")
  void unknownSkuIsRejected() {
    assertThrows(InvalidProductException.class, () -> checkout.process(Map.of("Z", 1)));
  }

  @Test
  @DisplayName("process replaces the basket each time")
  void processReplacesBasket() {
    assertProcess(Map.of("A", 1), 50);
    assertProcess(Map.of("A", 2), 100);
    assertProcess(Map.of("A", 3), 130);
  }

  @Nested
  @DisplayName("multipriced")
  class MultiPriced {

    @Test
    @DisplayName("3 A cost 130")
    void exactGroup() {
      assertProcess(Map.of("A", 3), 130);
    }

    @Test
    @DisplayName("4 A cost 180 (3 for 130 + 1 at 50)")
    void withLeftover() {
      assertProcess(Map.of("A", 4), 180);
    }

    @Test
    @DisplayName("2 B cost 125")
    void twoB() {
      assertProcess(Map.of("B", 2), 125);
    }

    @Test
    @DisplayName("counts matter, not scan order (same as B,A,B)")
    void orderIndependent() {
      assertProcess(Map.of("A", 1, "B", 2), 175);
    }
  }

  @Nested
  @DisplayName("buy N get 1 free")
  class BuyNGetOneFree {

    @Test
    @DisplayName("4 C cost 75 (pay 3)")
    void oneFreeItem() {
      assertProcess(Map.of("C", 4), 75);
    }

    @Test
    @DisplayName("5 C cost 100 (pay 3 + 1 unit)")
    void withRemainder() {
      assertProcess(Map.of("C", 5), 100);
    }

    @Test
    @DisplayName("3 C still cost 75 (no free item yet)")
    void noFreeYet() {
      assertProcess(Map.of("C", 3), 75);
    }
  }

  @Nested
  @DisplayName("meal deal")
  class MealDeal {

    @Test
    @DisplayName("D + E cost 300")
    void oneDeal() {
      assertProcess(Map.of("D", 1, "E", 1), 300);
    }

    @Test
    @DisplayName("2 D + 1 E cost 450 (one deal + leftover D)")
    void withLeftover() {
      assertProcess(Map.of("D", 2, "E", 1), 450);
    }

    @Test
    @DisplayName("two full deals cost 600")
    void twoDeals() {
      assertProcess(Map.of("D", 2, "E", 2), 600);
    }
  }

  @Nested
  @DisplayName("mixed basket")
  class Mixed {

    @Test
    @DisplayName("3A + 2B + 4C + D + E = 630")
    void fullWeekBasket() {
      assertProcess(Map.of("A", 3, "B", 2, "C", 4, "D", 1, "E", 1), 630);
    }
  }

  @Nested
  @DisplayName("injectable pricing rules")
  class InjectableRules {

    @Test
    @DisplayName("another week can pass different rules")
    void differentRulesChangeTotal() {
      PricingRules otherWeek =
          new PricingRules(
              List.of(new Product("A", BigDecimal.valueOf(50))),
              List.of(new MultiPricedPromotion("A", 2, BigDecimal.valueOf(80))));

      CheckoutUseCase otherCheckout = CheckoutFactory.start(otherWeek);
      assertEquals(
          0, otherCheckout.process(Map.of("A", 2)).compareTo(BigDecimal.valueOf(80)));
    }
  }

  private void assertProcess(Map<String, Integer> quantities, int expectedPence) {
    BigDecimal total = checkout.process(quantities);
    assertEquals(
        0,
        total.compareTo(BigDecimal.valueOf(expectedPence)),
        () -> "expected " + expectedPence + " but was " + total);
  }
}
