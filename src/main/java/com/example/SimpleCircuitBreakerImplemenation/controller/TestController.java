package com.example.SimpleCircuitBreakerImplemenation.controller;

import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreaker;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreakerRegistry;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception.MyTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author rishi
 */
@RestController
public class TestController {

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  private CircuitBreaker circuitBreaker;

  public TestController(CircuitBreakerRegistry circuitBreakerRegistry) {
    this.circuitBreakerRegistry = circuitBreakerRegistry;
    circuitBreaker = circuitBreakerRegistry.circuitBreaker("TestCircuitBreaker");
  }

  @GetMapping
  public String testEndpoint(@RequestParam(value = "success", required = false) Boolean success) {
    try {
      return circuitBreaker.performOperation(() -> computedResponse(success), this::defaultResponse);
    } catch (Exception e){
      return e.getMessage();
    }
  }

  private String computedResponse(Boolean success) {

    if (Objects.nonNull(success)) {
      if (success) {
        circuitBreaker.onSuccess();
        return "Success response";
      } else {
        throw new MyTimeoutException("Failure in Circuit Breaker");
      }
    } else {
      throw new RuntimeException("Not a failure in Circuit Breaker");
    }
  }

  private String defaultResponse() {
    return "Circuit Breaker in OPEN state. This is default response";
  }
}
