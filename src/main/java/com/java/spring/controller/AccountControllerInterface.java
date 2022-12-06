package com.java.spring.controller;

import com.java.spring.dto.AccountDto;
import com.java.spring.dto.PasswordDto;
import com.java.spring.dto.ValueDto;
import com.java.spring.model.Account;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Create an account", 
          content = { @Content(mediaType = "application/json", 
          schema = @Schema(implementation = Account.class)) }),
      @ApiResponse(responseCode = "400", description = "Wrong format",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Token not found",
          content = @Content)})
  public ResponseEntity<Account> create(
      @RequestBody AccountDto accountDto,
      @RequestHeader(value = "token", defaultValue = "") String token
  );

  @GetMapping("/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Find an account by id", 
          content = { @Content(mediaType = "application/json", 
          schema = @Schema(implementation = Account.class)) }),
      @ApiResponse(responseCode = "401", description = "Token not found",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Account not found",
          content = @Content)})
  public ResponseEntity<Account> findById(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String id
  );

  @PatchMapping("/balance/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Make withdrawals and deposits to the account", 
          content = { @Content(mediaType = "application/json", 
          schema = @Schema(implementation = Account.class)) }),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Account not found",
          content = @Content)})
  public ResponseEntity<Account> alterBalanceByAccountId(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String id,
      @RequestBody ValueDto value
  );

  @PatchMapping("/update/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad Request",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Account not found",
          content = @Content)})
  public ResponseEntity<Object> updateAccount(
      @PathVariable String id,
      @RequestHeader(value = "token", defaultValue = "") String token,
      @RequestBody AccountDto accountDto
  );

  @PatchMapping("/{idTransfer}/to/{idReceiver}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Make withdrawals and deposits to the account",
          content = { @Content(mediaType = "application/json",
          schema = @Schema(implementation = String.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Account not found",
          content = @Content)})
  public ResponseEntity<String> bankTransfer(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String idTransfer,
      @PathVariable String idReceiver,
      @RequestBody ValueDto transferDto
  );

  @DeleteMapping("/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Account not found",
          content = @Content)})
  public ResponseEntity<Object> delete(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable String id,
      @RequestBody PasswordDto password
  );
}
