package com.java.spring.controller;

import org.springframework.http.ResponseEntity;

public interface AccountControllerInterface<T, K> {
  public ResponseEntity<K> create(T object, String token);
}
