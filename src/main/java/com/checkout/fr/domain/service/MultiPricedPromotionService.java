package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.MultiPricedPromotion;
import com.checkout.fr.domain.model.PricingRules;
import com.checkout.fr.domain.model.Product;
import com.checkout.fr.domain.model.PromotionType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * buy n of them and which will cost you y. For example, item A might cost 50 pence individually but
 * this week we have a special offer where you can buy 3 As for £1.30
 */
public class MultiPricedPromotionService implements PromotionService {

  @Override
  public PromotionType getType() {
    return PromotionType.MULTI_PRICED;
  }

  @Override
  public BigDecimal discount(Map<String, Integer> quantities, PricingRules rules) {
    return rules.promotionsOfType(MultiPricedPromotion.class).stream()
        .map(promotion -> discountFor(promotion, quantities, rules))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal discountFor(
      MultiPricedPromotion promotion, Map<String, Integer> quantities, PricingRules rules) {

    return Optional.of(quantities.getOrDefault(promotion.getProductId(), 0))
        .map(quantityInBasket -> countCompleteGroups(quantityInBasket, promotion.getQuantity()))
        .filter(completeGroups -> completeGroups > 0)
        .map(
            completeGroups ->
                discountForGroups(
                    rules.requireProduct(promotion.getProductId()), promotion, completeGroups))
        .filter(discount -> discount.signum() > 0)
        .orElse(BigDecimal.ZERO);
  }

  private int countCompleteGroups(int quantityInBasket, int groupSize) {
    return (quantityInBasket <= 0 || groupSize <= 0) ? 0 : quantityInBasket / groupSize;
  }

  private BigDecimal discountForGroups(
      Product product, MultiPricedPromotion promotion, int completeGroups) {

    int itemsInOffer = completeGroups * promotion.getQuantity();
    BigDecimal priceWithoutOffer = product.getPrice().multiply(BigDecimal.valueOf(itemsInOffer));
    BigDecimal priceWithOffer =
        promotion.getSpecialPrice().multiply(BigDecimal.valueOf(completeGroups));
    return priceWithoutOffer.subtract(priceWithOffer);
  }
}
