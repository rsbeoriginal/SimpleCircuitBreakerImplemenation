package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker;

/**
 * @author rishi
 */
public interface CircuitBreakerRegistry {

  CircuitBreaker circuitBreaker(String circuitBreakerName);

}
