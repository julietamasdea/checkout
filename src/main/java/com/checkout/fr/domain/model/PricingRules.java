package com.checkout.fr.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Pricing rules for one checkout transaction: product catalog + promotions. Products do not carry
 * promotions; rules are passed in separately (kata extra point).
 */
@Getter
public class PricingRules {

  private final Map<String, Product> products;
  private final List<Promotion> promotions;

  public PricingRules(Collection<Product> products, Collection<Promotion> promotions) {
    this.products =
        products.stream()
            .collect(Collectors.toUnmodifiableMap(Product::getId, Function.identity()));
    this.promotions = List.copyOf(promotions);
  }

  public boolean contains(String sku) {
    return products.containsKey(sku);
  }

  public Product requireProduct(String sku) {
    Product product = products.get(sku);
    if (product == null) {
      throw new IllegalArgumentException("Unknown product: " + sku);
    }
    return product;
  }

  public <T extends Promotion> List<T> promotionsOfType(Class<T> type) {
    return promotions.stream().filter(type::isInstance).map(type::cast).toList();
  }
}
