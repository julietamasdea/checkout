package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.config.ThisWeekPricingRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CheckoutUseCaseTest {

    @Autowired
    private CheckoutFactory checkoutFactory;

    private CheckoutUseCase checkout;

    @BeforeEach
    void setUp() {
        checkout = checkoutFactory.start(ThisWeekPricingRules.create());
    }

    @Test
    void emptyCheckoutIsZero() {
        assertEquals(0, checkout.total().compareTo(BigDecimal.ZERO));
    }

    @Test
    void singleItemsUseUnitPrice() {
        checkout.scan("A");
        checkout.scan("C");
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(75)));
    }

    @Test
    void multiPricedAppliesForExactGroups() {
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(130)));
    }

    @Test
    void multiPricedAppliesWithLeftover() {
        // 4A => 3 for 130 + 1 for 50 = 180
        scan("A", 4);
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(180)));
    }

    @Test
    void multiPricedIsOrderIndependent() {
        // B, A, B => 2B for 125 + A for 50 = 175
        checkout.scan("B");
        checkout.scan("A");
        checkout.scan("B");
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(175)));
    }

    @Test
    void buyNGetOneFree() {
        // Buy 3 get 1 free: 4C => pay 3 => 75
        scan("C", 4);
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(75)));
    }

    @Test
    void buyNGetOneFreeWithRemainder() {
        // 5C => one free block (pay 3) + 1 unit = 100
        scan("C", 5);
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(100)));
    }

    @Test
    void mealDealApplies() {
        checkout.scan("D");
        checkout.scan("E");
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(300)));
    }

    @Test
    void mealDealWithLeftover() {
        // D, D, E => 1 deal (300) + 1 D (150) = 450
        checkout.scan("D");
        checkout.scan("D");
        checkout.scan("E");
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(450)));
    }

    @Test
    void mixedBasket() {
        // 3A (130) + 2B (125) + 4C (75) + D+E (300) = 630
        scan("A", 3);
        scan("B", 2);
        scan("C", 4);
        checkout.scan("D");
        checkout.scan("E");
        assertEquals(0, checkout.total().compareTo(BigDecimal.valueOf(630)));
    }

    @Test
    void unknownSkuIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> checkout.scan("Z"));
    }

    private void scan(String sku, int times) {
        for (int i = 0; i < times; i++) {
            checkout.scan(sku);
        }
    }
}
