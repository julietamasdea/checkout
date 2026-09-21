package com.checkout.fr.domain.model;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;

@Getter
public class MealDealPromotion extends Promotion {

    /** SKUs that must be bought together for the deal. */
    private final Set<String> requiredProductIds;

    /** Special price for one full meal deal, in pence. */
    private final BigDecimal dealPrice;

    public MealDealPromotion(Set<String> requiredProductIds, BigDecimal dealPrice) {
        super(PromotionType.MEAL_DEAL);
        this.requiredProductIds = Set.copyOf(requiredProductIds);
        this.dealPrice = dealPrice;
    }
}
