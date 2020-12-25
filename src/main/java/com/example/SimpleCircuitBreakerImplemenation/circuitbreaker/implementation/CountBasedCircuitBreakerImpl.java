package com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.implementation;

import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreaker;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreakerEvent;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreakerEventState;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.CircuitBreakerState;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception.MyTimeoutException;
import com.example.SimpleCircuitBreakerImplemenation.circuitbreaker.exception.PermissionNotAcquiredException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * @author rishi
 */
public class CountBasedCircuitBreakerImpl implements CircuitBreaker {

  private String name;
  private Integer windowSize = 10;
  private Integer failureRateThreshold = 50;
  private CircuitBreakerState currentState;
  private Queue<CircuitBreakerEvent> eventWindow = new LinkedList<>();
  private long latestTimestampInOpenState;
  private Long waitDurationInOpenState = Long.valueOf(1000 * 60 * 5);
  private int successCountInQueue = 0;
  private int failCountInQueue = 0;
  private List<Class<? extends Exception>> failureExceptionList =
      Arrays.asList(MyTimeoutException.class);

  public CountBasedCircuitBreakerImpl(String name) {
    this.name = name;
  }

  @Override
  public void acquirePermission() throws PermissionNotAcquiredException {
    if (CircuitBreakerState.CLOSE.equals(currentState)) {
      return;
    }
    computePermission();
  }

  private void computePermission() throws PermissionNotAcquiredException {
    if (CircuitBreakerState.OPEN.equals(currentState)) {
      long durationElapsedInOpenState = System.currentTimeMillis() - latestTimestampInOpenState;
      if (durationElapsedInOpenState >= waitDurationInOpenState) {
        updateCurrentState(CircuitBreakerState.CLOSE);
      } else {
        String message = String.format(
            "CircuitBreaker name : '%s' ; state : %s ; failCountInQueue : %d ; "
                + "durationElapsedInOpenState : %d ", name, currentState.name(), failCountInQueue,
            durationElapsedInOpenState);
        throw new PermissionNotAcquiredException(message);
      }
    }
  }

  @Override
  public void onSuccess() {
    addEventToQueue(CircuitBreakerEventState.SUCCESS);
  }

  @Override
  public void onError() {
    addEventToQueue(CircuitBreakerEventState.FAIL);
  }

  @Override
  public <T> T performOperation(Supplier<T> computedResponse, Supplier<T> defaultResponse) {
    try {
      acquirePermission();
      T computedValue = computedResponse.get();
      onSuccess();
      return computedValue;
    } catch (PermissionNotAcquiredException e) {
      return defaultResponse.get();
    } catch (Exception e){
      if (failureExceptionList.contains(e.getClass())){
        onError();
      } else {
        onSuccess();
      }
      throw e;
    }
  }

  private CircuitBreakerEvent createCircuitBreakerEvent(CircuitBreakerEventState state) {
    return CircuitBreakerEvent.builder().state(state).timestamp(System.currentTimeMillis()).build();
  }

  private void addEventToQueue(CircuitBreakerEventState state) {
    if (eventWindow.size() == windowSize) {
      if (CircuitBreakerEventState.FAIL.equals(eventWindow.poll().getState())) {
        failCountInQueue--;
      } else {
        successCountInQueue--;
      }
    }
    eventWindow.add(createCircuitBreakerEvent(state));
    if (CircuitBreakerEventState.FAIL.equals(state)) {
      failCountInQueue++;
    } else {
      successCountInQueue++;
    }
    computeCurrentState();
  }

  private void computeCurrentState() {
    int currentFailureRate = (failCountInQueue * 100 / windowSize);
    if (currentFailureRate >= failureRateThreshold) {
      updateCurrentState(CircuitBreakerState.OPEN);
      latestTimestampInOpenState = System.currentTimeMillis();
    } else {
      updateCurrentState(CircuitBreakerState.CLOSE);
    }
  }

  private void updateCurrentState(CircuitBreakerState state) {
    currentState = state;
  }

}
