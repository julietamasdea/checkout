package com.checkout.fr.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Product {

    /** SKU — unique id (A, B, C, ...). */
    private String id;

    private String name;

    /** Unit price in pence. */
    private BigDecimal price;
}
