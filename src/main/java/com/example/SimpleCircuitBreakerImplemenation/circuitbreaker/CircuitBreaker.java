package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker;

import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception.PermissionNotAcquiredException;

/**
 * @author rishi
 */
public interface CircuitBreaker {

  void acquirePermission() throws PermissionNotAcquiredException;

  void onSuccess();

  void onError();

}
