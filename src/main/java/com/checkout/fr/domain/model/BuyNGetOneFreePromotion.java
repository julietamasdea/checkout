package com.checkout.fr.domain.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/** Buy N get 1 free: for every block of (N + 1) items, one is free. */
public class BuyNGetOneFreePromotion extends Promotion {

  private final String productId;
  private final int buyQuantity;

  public BuyNGetOneFreePromotion(String productId, int buyQuantity) {
    super(PromotionType.BUY_N_GET_ONE_FREE);
    this.productId = productId;
    this.buyQuantity = buyQuantity;
  }

  public String getProductId() {
    return productId;
  }

  public int getBuyQuantity() {
    return buyQuantity;
  }

  @Override
  public BigDecimal discount(Map<String, Integer> quantities, PricingRules rules) {
    return Optional.of(quantities.getOrDefault(productId, 0))
        .map(quantityInBasket -> countFreeItems(quantityInBasket, buyQuantity))
        .filter(freeItems -> freeItems > 0)
        .map(
            freeItems ->
                rules.requireProduct(productId).price().multiply(BigDecimal.valueOf(freeItems)))
        .filter(discount -> discount.signum() > 0)
        .orElse(BigDecimal.ZERO);
  }

  private int countFreeItems(int quantityInBasket, int buyQuantity) {
    int blockSize = buyQuantity + 1;
    return (quantityInBasket <= 0 || buyQuantity <= 0) ? 0 : quantityInBasket / blockSize;
  }
}
