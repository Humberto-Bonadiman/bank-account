package com.java.spring.exception;

public class IncorrectFullNameLengthException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public IncorrectFullNameLengthException() {
    super("'fullName' length must be at least 7 characters long");
  }

}
