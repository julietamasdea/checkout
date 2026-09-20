package com.checkout.fr.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Product {

    private String id;
    private String name;
    private BigDecimal price;
    private PromotionType promotionType;
}
