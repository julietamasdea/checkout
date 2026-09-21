package com.checkout.fr.domain.model;

import lombok.Getter;

@Getter
public class BuyNGetOneFreePromotion extends Promotion {

    private final String productId;

    /** Number of items the customer pays for; the next one is free. */
    private final int buyQuantity;

    public BuyNGetOneFreePromotion(String productId, int buyQuantity) {
        super(PromotionType.BUY_N_GET_ONE_FREE);
        this.productId = productId;
        this.buyQuantity = buyQuantity;
    }
}
