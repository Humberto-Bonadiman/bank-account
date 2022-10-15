package com.java.spring.service;

import com.java.spring.dto.TransferDto;
import com.java.spring.dto.ValueDto;

public interface AccountInterface<T, K> {

  public K create(T object, String token);

  public K findById(String token, String id);

  public K alterBalanceByAccountId(String token, String id, ValueDto value);

  public String bankTransfer(String idTransfer, String idReceiver, String token, TransferDto transferDto);
}
