package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception;

/**
 * @author rishi
 */
public class MyTimeoutException extends RuntimeException {

  public MyTimeoutException(String message) {
    super(message);
  }
}
