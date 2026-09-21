package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.PromotionType;

import java.util.List;
import java.util.Map;

/**
 * Evaluates whether its promotion type applies to the current basket.
 * Must not mutate {@code quantities}. Returns zero or more results.
 */
public interface PromotionService {

    PromotionType getType();

    /**
     * @param quantities current basket counts by sku
     * @param rules      pricing rules for this transaction
     */
    List<PromotionResult> apply(Map<String, Integer> quantities, PricingRules rules);
}
