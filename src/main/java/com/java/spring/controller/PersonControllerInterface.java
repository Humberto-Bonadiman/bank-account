package com.java.spring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

/**
 * ControllerInterface.
 */
public interface PersonControllerInterface<T, K> {
  public ResponseEntity<K> create(T object);

  public ResponseEntity<List<K>> findAll(String token);

  public ResponseEntity<String> generateToken(T object);

}
