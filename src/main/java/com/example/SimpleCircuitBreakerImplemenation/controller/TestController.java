package com.example.SimpleCircuitBreakerImplemenation.controller;

import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreaker;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreakerRegistry;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception.PermissionNotAcquiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rishi
 */
@RestController
public class TestController {

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  private CircuitBreaker circuitBreaker;

  public TestController() {
    circuitBreaker = circuitBreakerRegistry.circuitBreaker("TestCircuitBreaker");
  }

  @GetMapping
  public String testEndpoint(@RequestParam("success") Boolean success) {
    try {
      circuitBreaker.acquirePermission();
      return computedResponse(success);
    } catch (PermissionNotAcquiredException e) {
      return defaultResponse();
    }
  }

  private String computedResponse(Boolean success) {
    if (success) {
      circuitBreaker.onSuccess();
      return "Success response";
    } else {
      circuitBreaker.onError();
      return "Error response";
    }
  }

  private String defaultResponse() {
    return "Circuit Breaker in OPEN state. This is default response";
  }
}
