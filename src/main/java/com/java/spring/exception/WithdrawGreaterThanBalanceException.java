package com.java.spring.exception;

public class WithdrawGreaterThanBalanceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public WithdrawGreaterThanBalanceException() {
    super("it is not possible to withdraw an amount greater than the balance");
  }

}
