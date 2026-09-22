package com.checkout.fr.domain.model;

import java.math.BigDecimal;

/** SKU and unit price in pence. */
public record Product(String id, BigDecimal price) {}
