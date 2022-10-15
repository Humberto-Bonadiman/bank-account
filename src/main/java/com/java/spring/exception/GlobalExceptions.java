package com.java.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.JWTVerificationException;

@ControllerAdvice
public class GlobalExceptions {

  @ExceptionHandler({
      IncorrectFullNameLengthException.class,
      IncorrectCpfLengthException.class,
      NullPointerException.class,
      DatetimeConvertionException.class,
      IncorrectEmailFormat.class,
      PasswordLengthException.class,
      WithdrawGreaterThanBalanceException.class,
  })
  public ResponseEntity<Object> handlerBadRequest(RuntimeException exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({PersonAlreadyRegisteredException.class})
  public ResponseEntity<Object> handlerConflict(RuntimeException exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.CONFLICT);
  }

  @ExceptionHandler({
      JWTVerificationException.class,
      TokenNotFoundException.class,
      ValueDepositException.class,
      NegativeValueException.class,
      InsufficientBalanceException.class,
      IncorrectPasswordException.class,
      DifferentIdException.class
  })
  public ResponseEntity<Object> handlerUnauthorized(RuntimeException exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({PersonNotRegisteredException.class, AccountNotFoundException.class})
  public ResponseEntity<Object> handlerNotFound(RuntimeException exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.NOT_FOUND);
  }
}
