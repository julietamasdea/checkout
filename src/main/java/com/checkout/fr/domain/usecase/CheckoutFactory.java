package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.service.BuyNGetOneFreePromotionService;
import com.checkout.fr.domain.service.MealDealPromotionService;
import com.checkout.fr.domain.service.MultiPricedPromotionService;
import com.checkout.fr.domain.service.PromotionService;
import java.util.List;

/** Creates a checkout with the default promotion services. */
public final class CheckoutFactory {

  private static final List<PromotionService> DEFAULT_SERVICES =
      List.of(
          new MultiPricedPromotionService(),
          new BuyNGetOneFreePromotionService(),
          new MealDealPromotionService());

  private CheckoutFactory() {}

  public static CheckoutUseCase start(PricingRules pricingRules) {
    return new CheckoutUseCase(pricingRules, DEFAULT_SERVICES);
  }
}
