package com.java.spring.exception;

public class CpfNotNumericException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CpfNotNumericException() {
    super("cpf can only contain numbers");
  }

}
