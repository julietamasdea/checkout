package com.checkout.fr.infrastructure.checkout;

import com.checkout.fr.infrastructure.controller.CheckoutRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Maps the HTTP checkout request to the quantity map expected by domain. HTTP validation stays
 * here; it does not call {@code CheckoutUseCase}.
 */
@Component
public class CheckoutMapper {

  /** Validates the request and returns sku → quantity. */
  public Map<String, Integer> toQuantityMap(CheckoutRequest request) {
    if (request == null || request.items() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items is required");
    }
    return toQuantityMap(request.items());
  }

  /** Turns ["B","A","B"] into {A:1, B:2}. */
  private Map<String, Integer> toQuantityMap(List<String> items) {
    Map<String, Integer> quantities = new HashMap<>();
    items.forEach(sku -> processSku(sku, quantities));
    return quantities;
  }

  private void processSku(String sku, Map<String, Integer> quantities) {
    if (sku == null || sku.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sku must not be blank");
    }
    quantities.merge(sku, 1, Integer::sum);
  }
}
