package com.checkout.fr.infrastructure.checkout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.checkout.fr.infrastructure.controller.CheckoutRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CheckoutMapperTest {

  private final CheckoutMapper mapper = new CheckoutMapper();

  @Test
  @DisplayName("counts repeated SKUs")
  void mapsListToQuantityMap() {
    Map<String, Integer> quantities =
        mapper.toQuantityMap(new CheckoutRequest(List.of("B", "A", "B")));

    assertEquals(Map.of("A", 1, "B", 2), quantities);
  }

  @Test
  @DisplayName("empty list becomes empty map")
  void emptyList() {
    assertTrue(mapper.toQuantityMap(new CheckoutRequest(List.of())).isEmpty());
  }

  @Test
  @DisplayName("null request is bad request")
  void nullRequest() {
    ResponseStatusException ex =
        assertThrows(ResponseStatusException.class, () -> mapper.toQuantityMap(null));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("items is required", ex.getReason());
  }

  @Test
  @DisplayName("null items is bad request")
  void nullItems() {
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class, () -> mapper.toQuantityMap(new CheckoutRequest(null)));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("items is required", ex.getReason());
  }

  @Test
  @DisplayName("blank sku is bad request")
  void blankSku() {
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> mapper.toQuantityMap(new CheckoutRequest(List.of("A", " "))));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("sku must not be blank", ex.getReason());
  }

  @Test
  @DisplayName("null sku is bad request")
  void nullSku() {
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> mapper.toQuantityMap(new CheckoutRequest(java.util.Arrays.asList("A", null))));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("sku must not be blank", ex.getReason());
  }
}
