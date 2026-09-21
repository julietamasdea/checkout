package com.checkout.fr.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MultiPricedPromotion extends Promotion {

    private final String productId;

    /** Buy this many items to get the special price. */
    private final int quantity;

    /** Special price for the whole group, in pence. */
    private final BigDecimal specialPrice;

    public MultiPricedPromotion(String productId, int quantity, BigDecimal specialPrice) {
        super(PromotionType.MULTI_PRICED);
        this.productId = productId;
        this.quantity = quantity;
        this.specialPrice = specialPrice;
    }
}
