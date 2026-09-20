package com.checkout.fr.domain.service;

import com.checkout.fr.domain.model.Product;

import java.math.BigInteger;
import java.util.Map;

public interface PromotionService {

    void applyDiscount(Map<Product, BigInteger> products);
}
