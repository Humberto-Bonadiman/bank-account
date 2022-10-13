package com.java.spring.exception;

public class TokenNotFoundException extends RuntimeException  {
  private static final long serialVersionUID = 1L;

  public TokenNotFoundException() {
    super("Token not found");
  }

}
