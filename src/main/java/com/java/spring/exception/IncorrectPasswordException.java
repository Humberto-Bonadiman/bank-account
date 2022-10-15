package com.java.spring.exception;

public class IncorrectPasswordException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public IncorrectPasswordException() {
    super("invalid password authentication");
  }

}
