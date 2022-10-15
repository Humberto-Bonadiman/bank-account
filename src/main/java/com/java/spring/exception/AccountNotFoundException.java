package com.java.spring.exception;

public class AccountNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AccountNotFoundException() {
    super("account not found");
  }

}
