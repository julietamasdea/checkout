package com.checkout.fr.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.checkout.fr.domain.config.ThisWeekPricingRules;
import com.checkout.fr.domain.model.MultiPricedPromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
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
    assertTotal(0);
  }

  @Test
  @DisplayName("items without a full offer use unit price")
  void singleItemsUseUnitPrice() {
    checkout.addProduct("A");
    checkout.addProduct("C");
    assertTotal(75);
  }

  @Test
  @DisplayName("unknown SKU is rejected")
  void unknownSkuIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> checkout.addProduct("Z"));
  }

  @Test
  @DisplayName("total updates after every addProduct")
  void runningTotalIsRecalculatedOnEachAdd() {
    checkout.addProduct("A");
    assertTotal(50);

    checkout.addProduct("A");
    assertTotal(100);

    checkout.addProduct("A");
    assertTotal(130);
  }

  @Nested
  @DisplayName("multipriced")
  class MultiPriced {

    @Test
    @DisplayName("3 A cost 130")
    void exactGroup() {
      add("A", 3);
      assertTotal(130);
    }

    @Test
    @DisplayName("4 A cost 180 (3 for 130 + 1 at 50)")
    void withLeftover() {
      add("A", 4);
      assertTotal(180);
    }

    @Test
    @DisplayName("2 B cost 125")
    void twoB() {
      add("B", 2);
      assertTotal(125);
    }

    @Test
    @DisplayName("scan order does not matter (B, A, B)")
    void orderIndependent() {
      checkout.addProduct("B");
      checkout.addProduct("A");
      checkout.addProduct("B");
      assertTotal(175);
    }
  }

  @Nested
  @DisplayName("buy N get 1 free")
  class BuyNGetOneFree {

    @Test
    @DisplayName("4 C cost 75 (pay 3)")
    void oneFreeItem() {
      add("C", 4);
      assertTotal(75);
    }

    @Test
    @DisplayName("5 C cost 100 (pay 3 + 1 unit)")
    void withRemainder() {
      add("C", 5);
      assertTotal(100);
    }

    @Test
    @DisplayName("3 C still cost 75 (no free item yet)")
    void noFreeYet() {
      add("C", 3);
      assertTotal(75);
    }
  }

  @Nested
  @DisplayName("meal deal")
  class MealDeal {

    @Test
    @DisplayName("D + E cost 300")
    void oneDeal() {
      checkout.addProduct("D");
      checkout.addProduct("E");
      assertTotal(300);
    }

    @Test
    @DisplayName("D, D, E cost 450 (one deal + leftover D)")
    void withLeftover() {
      checkout.addProduct("D");
      checkout.addProduct("D");
      checkout.addProduct("E");
      assertTotal(450);
    }

    @Test
    @DisplayName("two full deals cost 600")
    void twoDeals() {
      add("D", 2);
      add("E", 2);
      assertTotal(600);
    }
  }

  @Nested
  @DisplayName("mixed basket")
  class Mixed {

    @Test
    @DisplayName("3A + 2B + 4C + D + E = 630")
    void fullWeekBasket() {
      add("A", 3);
      add("B", 2);
      add("C", 4);
      checkout.addProduct("D");
      checkout.addProduct("E");
      assertTotal(630);
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
      otherCheckout.addProduct("A");
      otherCheckout.addProduct("A");

      assertEquals(0, otherCheckout.total().compareTo(BigDecimal.valueOf(80)));
    }
  }

  private void add(String sku, int times) {
    for (int i = 0; i < times; i++) {
      checkout.addProduct(sku);
    }
  }

  private void assertTotal(int expectedPence) {
    assertEquals(
        0,
        checkout.total().compareTo(BigDecimal.valueOf(expectedPence)),
        () -> "expected " + expectedPence + " but was " + checkout.total());
  }
}
