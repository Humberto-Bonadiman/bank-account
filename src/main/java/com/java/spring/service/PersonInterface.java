package com.java.spring.service;

import java.util.List;

public interface PersonInterface<T, K> {

  public K create(T object);

  public K generateToken(String fullName);

  public List<K> findAll(String token);

}
