package com.java.spring.service;

public interface AccountInterface<T, K> {

  public K create(T object, String token);
}
