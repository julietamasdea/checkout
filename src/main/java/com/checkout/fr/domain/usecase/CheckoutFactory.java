package com.checkout.fr.domain.usecase;

import com.checkout.fr.domain.model.PricingRules;

/** Creates a checkout for the given pricing rules. */
public final class CheckoutFactory {

  private CheckoutFactory() {}

  public static CheckoutUseCase start(PricingRules pricingRules) {
    return new CheckoutUseCase(pricingRules);
  }
}
