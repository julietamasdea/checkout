package com.checkout.fr.domain.model.exception;

public class InvalidProductException extends RuntimeException {
  public InvalidProductException(String message) {
    super(message);
  }
}
