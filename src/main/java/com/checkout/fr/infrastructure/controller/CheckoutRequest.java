package com.checkout.fr.infrastructure.controller;

import java.util.List;

/** Request: full basket of SKUs in any order, e.g. ["B", "A", "B"]. */
public record CheckoutRequest(List<String> items) {}
