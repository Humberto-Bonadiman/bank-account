package com.java.spring.exception;

public class IncorrectEmailFormat extends RuntimeException  {
  private static final long serialVersionUID = 1L;

  public IncorrectEmailFormat() {
    super("'email' must be a valid email");
  }
}
