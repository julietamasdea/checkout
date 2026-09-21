package com.checkout.fr.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Candidate application of one promotion rule.
 * {@code discount} is how much cheaper this is vs paying unit prices for {@code affectedItems}.
 */
public record PromotionResult(
        List<AffectedItem> affectedItems,
        BigDecimal discount
) {

    public PromotionResult {
        affectedItems = List.copyOf(affectedItems);
        if (discount == null) {
            throw new IllegalArgumentException("discount is required");
        }
        if (discount.signum() < 0) {
            throw new IllegalArgumentException("discount must be >= 0");
        }
    }

    public boolean isEmpty() {
        return affectedItems.isEmpty() || discount.signum() == 0;
    }

    public Set<String> affectedSkus() {
        return affectedItems.stream()
                .map(AffectedItem::sku)
                .collect(Collectors.toUnmodifiableSet());
    }
}
