package com.java.spring.exception;

public class DatetimeConvertionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DatetimeConvertionException() {
    super("'birthDate' must be in the format 'dd/mm/yyyy'");
  }

}
