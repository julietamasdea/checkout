package com.checkout.fr.domain.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Product {

    /** SKU — unique id (A, B, C, ...). */
    private String id;

    /** Unit price in pence. */
    private BigDecimal price;
}
