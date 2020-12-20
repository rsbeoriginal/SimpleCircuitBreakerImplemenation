package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.implementation;

import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreaker;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreakerRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rishi
 */
public class CircuitBreakerRegistryImpl implements CircuitBreakerRegistry {

  private Map<String, CircuitBreaker> circuitBreakerMap;

  public CircuitBreakerRegistryImpl() {
    circuitBreakerMap = new HashMap<>();
  }

  @Override
  public CircuitBreaker circuitBreaker(String circuitBreakerName) {
    return circuitBreakerMap.containsKey(circuitBreakerName) ?
        circuitBreakerMap.get(circuitBreakerName) :
        addToRegistry(circuitBreakerName, createCircuitBreaker(circuitBreakerName));
  }

  private CircuitBreaker addToRegistry(String circuitBreakerName, CircuitBreaker circuitBreaker) {
    circuitBreakerMap.put(circuitBreakerName, circuitBreaker);
    return circuitBreakerMap.get(circuitBreakerName);
  }

  private CircuitBreaker createCircuitBreaker(String circuitBreakerName) {
    return new CountBasedCircuitBreakerImpl(circuitBreakerName);
  }
}
