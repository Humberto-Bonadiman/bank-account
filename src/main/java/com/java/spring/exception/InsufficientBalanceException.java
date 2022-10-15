package com.java.spring.exception;

public class InsufficientBalanceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InsufficientBalanceException() {
    super("Insufficient balance to complete the transfer");
  }
}
