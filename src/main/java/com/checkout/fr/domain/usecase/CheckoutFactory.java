package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.service.PromotionService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Creates a new checkout transaction with the given pricing rules.
 */
@Component
public class CheckoutFactory {

    private final List<PromotionService> promotionServices;

    public CheckoutFactory(List<PromotionService> promotionServices) {
        this.promotionServices = promotionServices;
    }

    public CheckoutUseCase start(PricingRules pricingRules) {
        return new CheckoutUseCase(pricingRules, promotionServices);
    }
}
