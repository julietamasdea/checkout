package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.BuyNGetOneFreePromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.PromotionType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/** Buy N get 1 free: for every block of (N + 1) items, one is free. */
public class BuyNGetOneFreePromotionService implements PromotionService {

  @Override
  public PromotionType getType() {
    return PromotionType.BUY_N_GET_ONE_FREE;
  }

  @Override
  public BigDecimal discount(Map<String, Integer> quantities, PricingRules rules) {
    return rules.promotionsOfType(BuyNGetOneFreePromotion.class).stream()
        .map(promotion -> discountFor(promotion, quantities, rules))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal discountFor(
      BuyNGetOneFreePromotion promotion, Map<String, Integer> quantities, PricingRules rules) {

    return Optional.of(quantities.getOrDefault(promotion.getProductId(), 0))
        .map(quantityInBasket -> countFreeItems(quantityInBasket, promotion.getBuyQuantity()))
        .filter(freeItems -> freeItems > 0)
        .map(
            freeItems ->
                discountForFreeItems(
                    rules.requireProduct(promotion.getProductId()), freeItems))
        .filter(discount -> discount.signum() > 0)
        .orElse(BigDecimal.ZERO);
  }

  private int countFreeItems(int quantityInBasket, int buyQuantity) {
    int blockSize = buyQuantity + 1;
    return (quantityInBasket <= 0 || buyQuantity <= 0) ? 0 : quantityInBasket / blockSize;
  }

  private BigDecimal discountForFreeItems(Product product, int freeItems) {
    return product.getPrice().multiply(BigDecimal.valueOf(freeItems));
  }
}
