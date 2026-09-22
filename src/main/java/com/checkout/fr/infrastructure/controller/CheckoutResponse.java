package com.checkout.fr.infrastructure.controller;

import java.math.BigDecimal;

/** Response total in pence. */
public record CheckoutResponse(BigDecimal total) {}
