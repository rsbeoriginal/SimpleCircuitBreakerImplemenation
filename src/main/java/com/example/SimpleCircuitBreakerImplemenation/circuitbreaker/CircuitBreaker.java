package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker;

import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception.PermissionNotAcquiredException;

import java.util.function.Supplier;

/**
 * @author rishi
 */
public interface CircuitBreaker {

  void acquirePermission() throws PermissionNotAcquiredException;

  void onSuccess();

  void onError();

  <T> T performOperation(Supplier<T> computedResponse, Supplier<T> defaultResponse);
}
