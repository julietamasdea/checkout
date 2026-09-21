package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.service.PromotionResult;
import com.checkout.fr.domain.service.PromotionService;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checkout for one transaction.
 * Pricing rules (catalog + promotions) are passed at construction time.
 *
 * Assumption: each product has at most one promotion; promotions do not overlap on SKUs.
 * Each {@link #addProduct} updates the quantity map and recalculates {@link #finalPrice}.
 */
@Getter
public class CheckoutUseCase {

    private final PricingRules pricingRules;
    private final List<PromotionService> promotionServices;
    private final Map<String, Integer> scannedItems = new HashMap<>();
    private BigDecimal finalPrice = BigDecimal.ZERO;

    public CheckoutUseCase(PricingRules pricingRules, List<PromotionService> promotionServices) {
        this.pricingRules = pricingRules;
        this.promotionServices = List.copyOf(promotionServices);
    }

    /**
     * Adds one product to the basket and recalculates the running total.
     */
    public void addProduct(String productId) {
        if (!pricingRules.contains(productId)) {
            throw new IllegalArgumentException("Unknown product: " + productId);
        }
        scannedItems.merge(productId, 1, Integer::sum);
        finalPrice = recalculate();
    }

    public BigDecimal total() {
        return finalPrice;
    }

    private BigDecimal recalculate() {
        List<PromotionResult> applied = promotionServices.stream()
                .map(service -> service.apply(scannedItems, pricingRules))
                .flatMap(List::stream)
                .filter(result -> !result.isEmpty())
                .toList();

        BigDecimal unitTotal = scannedItems.entrySet().stream()
                .map(entry -> pricingRules.requireProduct(entry.getKey()).getPrice()
                        .multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = applied.stream()
                .map(PromotionResult::discount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return unitTotal.subtract(totalDiscount);
    }
}
