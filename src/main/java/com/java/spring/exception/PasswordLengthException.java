package com.java.spring.exception;

public class PasswordLengthException extends RuntimeException  {
  private static final long serialVersionUID = 1L;

  public PasswordLengthException() {
    super("'password' length must be at least 6 characters long");
  }
}
