package com.java.spring.exception;

public class NegativeValueException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NegativeValueException() {
    super("it is not possible to transfer negative values");
  }

}
