package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.Product;

import java.math.BigInteger;
import java.util.Map;

public class MultiPricedPromotionService implements PromotionService {

    @Override
    public void applyDiscount(Map<Product, BigInteger> products) {
        products.forEach(x, y);
    }
}
