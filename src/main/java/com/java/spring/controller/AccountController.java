package com.java.spring.controller;

import com.java.spring.dto.AccountDto;
import com.java.spring.dto.PasswordDto;
import com.java.spring.dto.ValueDto;
import com.java.spring.model.Account;
import com.java.spring.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@Tag(name = "Account endpoint")
public class AccountController implements AccountControllerInterface {

  @Autowired
  AccountService accountService;

  public ResponseEntity<Account> create(AccountDto accountDto, String token) {
    return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(accountDto, token));
  }

  public ResponseEntity<Account> findById(String token, String id) {
    return ResponseEntity.status(HttpStatus.OK).body(accountService.findById(token, id));
  }

  public ResponseEntity<Account> alterBalanceByAccountId(
      String token,
      String id,
      ValueDto value
  ) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(accountService.alterBalanceByAccountId(token, id, value));
  }

  /**
   * bank transfer controller.
   */
  public ResponseEntity<String> bankTransfer(
      String token,
      String idTransfer,
      String idReceiver,
      ValueDto transferDto
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

  public ResponseEntity<Object> updateAccount(
      String id,
      String token,
      AccountDto accountDto
  ) {
    accountService.updateAccount(id, token, accountDto);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  public ResponseEntity<Object> delete(
      String token,
      String id,
      PasswordDto password
  ) {
    accountService.delete(id, token, password);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
