package com.java.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.spring.dto.AccountDto;
import com.java.spring.exception.TokenNotFoundException;
import com.java.spring.model.Account;
import com.java.spring.service.AccountService;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController implements AccountControllerInterface<AccountDto, Account> {

  @Autowired
  AccountService accountService;

  @PostMapping
  public ResponseEntity<Account> create(
    @RequestBody AccountDto accountDto,
    @RequestHeader(value="token", defaultValue = "") String token
  ) {
    if (token == "") throw new TokenNotFoundException();
    return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(accountDto, token));
  }
}
