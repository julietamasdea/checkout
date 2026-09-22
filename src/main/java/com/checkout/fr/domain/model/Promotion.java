package com.checkout.fr.domain.model;

import java.math.BigDecimal;
import java.util.Map;

public abstract class Promotion {

  private final PromotionType promotionType;

  protected Promotion(PromotionType promotionType) {
    this.promotionType = promotionType;
  }

  public PromotionType getPromotionType() {
    return promotionType;
  }

  /** Discount in pence vs unit prices for this offer (0 if it does not apply). */
  public abstract BigDecimal discount(Map<String, Integer> quantities, PricingRules rules);
}
