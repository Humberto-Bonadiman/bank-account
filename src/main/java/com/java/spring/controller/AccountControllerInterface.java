package com.java.spring.controller;

import com.java.spring.dto.AccountDto;
import com.java.spring.dto.PasswordDto;
import com.java.spring.dto.ValueDto;
import com.java.spring.model.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/account")
public interface AccountControllerInterface {

  @PostMapping
  public ResponseEntity<Account> create(
      @RequestBody AccountDto accountDto,
      @RequestHeader(value = "token", defaultValue = "") String token
  );

  @GetMapping("/{id}")
  public ResponseEntity<Account> findById(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String id
  );

  @PatchMapping("/balance/{id}")
  public ResponseEntity<Account> alterBalanceByAccountId(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String id,
      @RequestBody ValueDto value
  );

  @PatchMapping("/update/{id}")
  public ResponseEntity<Object> updateAccount(
      @PathVariable String id,
      @RequestHeader(value = "token", defaultValue = "") String token,
      @RequestBody AccountDto accountDto
  );

  @PatchMapping("/{idTransfer}/to/{idReceiver}")
  public ResponseEntity<String> bankTransfer(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String idTransfer,
      @PathVariable String idReceiver,
      @RequestBody ValueDto transferDto
  );

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> delete(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String id,
      @RequestBody PasswordDto password
  );
}
