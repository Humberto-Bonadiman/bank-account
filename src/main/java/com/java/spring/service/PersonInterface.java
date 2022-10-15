package com.java.spring.service;

import java.util.List;

public interface PersonInterface<T, K> {

  public K create(T object);

  public String generateToken(T object);

  public List<K> findAll(String token);

  public void deleteById(String token, Long id);

}
