package com.java.spring.exception;

public class IncorrectCpfLengthException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public IncorrectCpfLengthException() {
    super("'cpf' length must be 11 characters long");
  }

}
