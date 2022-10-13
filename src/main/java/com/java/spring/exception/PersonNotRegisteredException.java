package com.java.spring.exception;

public class PersonNotRegisteredException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PersonNotRegisteredException() {
    super("unregistered person");
  }

}
