package com.java.spring.controller;

import com.java.spring.dto.AccountDto;
import com.java.spring.dto.PasswordDto;
import com.java.spring.dto.ValueDto;
import org.springframework.http.ResponseEntity;

public interface AccountControllerInterface<T, K> {

  public ResponseEntity<K> create(T object, String token);

  public ResponseEntity<K> findById(String token, String id);

  public ResponseEntity<K> alterBalanceByAccountId(String token, String id, ValueDto valueDto);

  public ResponseEntity<Object> updateAccount(
      String id, String token, AccountDto accountDto);

  public ResponseEntity<String> bankTransfer(
      String token,
      String idTransfer,
      String idReceiver,
      ValueDto valueDto
  );

  public ResponseEntity<Object> delete(String token, String id, PasswordDto password);
}
