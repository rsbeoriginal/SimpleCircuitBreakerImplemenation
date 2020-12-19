package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker;

import lombok.Builder;
import lombok.Getter;

/**
 * @author rishi
 */
@Builder
@Getter
public class CircuitBreakerEvent {

  private CircuitBreakerEventState state;
  private Long timestamp;

}
