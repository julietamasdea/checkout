package com.checkout.fr.domain.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/** Buy n items for a special price. Example: A costs 50 each, or 3 for 130. */
public class MultiPricedPromotion extends Promotion {

  private final String productId;
  private final int quantity;
  private final BigDecimal specialPrice;

  public MultiPricedPromotion(String productId, int quantity, BigDecimal specialPrice) {
    super(PromotionType.MULTI_PRICED);
    this.productId = productId;
    this.quantity = quantity;
    this.specialPrice = specialPrice;
  }

  public String getProductId() {
    return productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getSpecialPrice() {
    return specialPrice;
  }

  @Override
  public BigDecimal discount(Map<String, Integer> quantities, PricingRules rules) {
    return Optional.of(quantities.getOrDefault(productId, 0))
        .map(quantityInBasket -> countCompleteGroups(quantityInBasket, quantity))
        .filter(completeGroups -> completeGroups > 0)
        .map(completeGroups -> discountForGroups(rules.requireProduct(productId), completeGroups))
        .filter(discount -> discount.signum() > 0)
        .orElse(BigDecimal.ZERO);
  }

  private int countCompleteGroups(int quantityInBasket, int groupSize) {
    return (quantityInBasket <= 0 || groupSize <= 0) ? 0 : quantityInBasket / groupSize;
  }

  private BigDecimal discountForGroups(Product product, int completeGroups) {
    int itemsInOffer = completeGroups * quantity;
    BigDecimal priceWithoutOffer = product.price().multiply(BigDecimal.valueOf(itemsInOffer));
    BigDecimal priceWithOffer = specialPrice.multiply(BigDecimal.valueOf(completeGroups));
    return priceWithoutOffer.subtract(priceWithOffer);
  }
}
