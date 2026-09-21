package com.checkout.fr.domain.service;

/**
 * One basket line touched by a promotion candidate.
 */
public record AffectedItem(String sku, int quantity) {

    public AffectedItem {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
    }
}
