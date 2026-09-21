package com.checkout.fr.domain.model;

import lombok.Getter;

@Getter
public abstract class Promotion {

    private final PromotionType promotionType;

    protected Promotion(PromotionType promotionType) {
        this.promotionType = promotionType;
    }
}
