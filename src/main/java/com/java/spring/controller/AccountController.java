package com.java.spring.controller;

import com.java.spring.dto.AccountDto;
import com.java.spring.dto.PasswordDto;
import com.java.spring.dto.ValueDto;
import com.java.spring.model.Account;
import com.java.spring.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account endpoint")
@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController implements AccountControllerInterface<AccountDto, Account> {

  @Autowired
  AccountService accountService;

  @Operation(summary = "Create an account")
  @PostMapping
  public ResponseEntity<Account> create(
    @RequestBody AccountDto accountDto,
    @RequestHeader(value="token", defaultValue = "") String token
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(accountDto, token));
  }

  @Operation(summary = "Find an account by id")
  @GetMapping("/{id}")
  public ResponseEntity<Account> findById(
    @RequestHeader(value="token", defaultValue = "") String token,
    @PathVariable String id
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(accountService.findById(token, id));
  }

  @Operation(summary = "Make withdrawals and deposits to the account by id")
  @PatchMapping("/balance/{id}")
  public ResponseEntity<Account> alterBalanceByAccountId(
    @RequestHeader(value="token", defaultValue = "") String token,
    @PathVariable String id,
    @RequestBody ValueDto value
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(accountService.alterBalanceByAccountId(token, id, value));
  }

  @Operation(summary = "Transfer between accounts by their ids")
  @PatchMapping("/{idTransfer}/to/{idReceiver}")
  public ResponseEntity<String> bankTransfer(
    @RequestHeader(value="token", defaultValue = "") String token,
    @PathVariable String idTransfer,
    @PathVariable String idReceiver,
    @RequestBody ValueDto transferDto
  ) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(accountService.bankTransfer(
            idTransfer,
            idReceiver,
            token,
            transferDto
        )
    );
  }

  @Operation(summary = "Change account data through id")
  @PatchMapping("/update/{id}")
  public ResponseEntity<Object> updateAccount(
    @PathVariable String id,
    @RequestHeader(value="token", defaultValue = "") String token,
    @RequestBody AccountDto accountDto
  ) {
    accountService.updateAccount(id, token, accountDto);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Delete account data by id")
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> delete(
    @RequestHeader(value="token", defaultValue = "") String token,
    @PathVariable String id,
    @RequestBody PasswordDto password
  ) {
    accountService.delete(id, token, password);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
