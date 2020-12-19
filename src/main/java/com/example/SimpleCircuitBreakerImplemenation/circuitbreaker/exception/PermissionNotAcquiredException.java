package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception;

/**
 * @author rishi
 */
public class PermissionNotAcquiredException extends Exception {

  public PermissionNotAcquiredException(String message) {
    super(message);
  }

}
