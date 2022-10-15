package com.java.spring.exception;

public class DifferentIdException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DifferentIdException() {
    super("provided id is different from the id found in the token");
  }
}
