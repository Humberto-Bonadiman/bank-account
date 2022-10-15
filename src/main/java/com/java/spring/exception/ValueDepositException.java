package com.java.spring.exception;

public class ValueDepositException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ValueDepositException() {
    super("It is not possible to make deposits above R$ 2000.00");
  }

}
