package com.java.spring.exception;

public class PersonAlreadyRegisteredException extends RuntimeException  {
  private static final long serialVersionUID = 1L;

  public PersonAlreadyRegisteredException() {
    super("person already registered");
  }

}
