package com.java.spring.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptions {

  @ExceptionHandler({
      IncorrectFullNameLengthException.class,
      IncorrectCpfLengthException.class,
      DatetimeConvertionException.class,
      IncorrectEmailFormat.class,
      PasswordLengthException.class,
      WithdrawGreaterThanBalanceException.class,
      CpfNotNumericException.class
  })
  public ResponseEntity<Object> handlerBadRequest(Exception exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({PersonAlreadyRegisteredException.class})
  public ResponseEntity<Object> handlerConflict(Exception exception) {
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
  public ResponseEntity<Object> handlerUnauthorized(Exception exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({PersonNotRegisteredException.class, AccountNotFoundException.class})
  public ResponseEntity<Object> handlerNotFound(Exception exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.NOT_FOUND);
  }
}
