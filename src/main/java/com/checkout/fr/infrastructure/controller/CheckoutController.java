package com.checkout.fr.infrastructure.controller;

import com.checkout.fr.domain.config.ThisWeekPricingRules;
import com.checkout.fr.domain.model.exception.InvalidProductException;
import com.checkout.fr.domain.usecase.CheckoutFactory;
import com.checkout.fr.domain.usecase.CheckoutUseCase;
import com.checkout.fr.infrastructure.checkout.CheckoutMapper;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Stateless checkout: maps the request, then calls domain. */
@RestController
@RequestMapping("/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

  private final CheckoutMapper checkoutMapper;

  public CheckoutController(CheckoutMapper checkoutMapper) {
    this.checkoutMapper = checkoutMapper;
  }

  @PostMapping
  public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
    Map<String, Integer> quantities = checkoutMapper.toQuantityMap(request);

    try {
      CheckoutUseCase useCase = CheckoutFactory.start(ThisWeekPricingRules.create());
      return ResponseEntity.ok(new CheckoutResponse(useCase.process(quantities)));
    } catch (InvalidProductException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
  }
}
