package com.checkout.fr.infrastructure.controller;

import com.checkout.fr.domain.config.ThisWeekPricingRules;
import com.checkout.fr.domain.usecase.CheckoutFactory;
import com.checkout.fr.domain.usecase.CheckoutUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stateless checkout: each request sends the full list of scanned SKUs and gets the total back. No
 * session — the basket is the request body.
 */
@RestController
@RequestMapping("/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

  @PostMapping
  public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
    if (request == null || request.items() == null) {
      return ResponseEntity.badRequest().build();
    }

    CheckoutUseCase checkout = CheckoutFactory.start(ThisWeekPricingRules.create());
    for (String sku : request.items()) {
      checkout.addProduct(sku);
    }

    return ResponseEntity.ok(new CheckoutResponse(checkout.total()));
  }
}
