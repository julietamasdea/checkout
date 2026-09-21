package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.PromotionType;
import java.math.BigDecimal;
import java.util.Map;

/** Calculates the total discount for one promotion type. */
public interface PromotionService {

  PromotionType getType();

  /**
   * @param quantities current basket counts by sku
   * @param rules pricing rules for this transaction
   * @return total discount in pence (0 if the offer does not apply)
   */
  BigDecimal discount(Map<String, Integer> quantities, PricingRules rules);
}
