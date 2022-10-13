package com.java.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptions {

  @ExceptionHandler({
      IncorrectFullNameLengthException.class,
      IncorrectCpfLengthException.class,
      NullPointerException.class,
  })
  public ResponseEntity<Object> handlerBadRequest(RuntimeException exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({PersonAlreadyRegisteredException.class})
  public ResponseEntity<Object> handlerConflict(RuntimeException exception) {
    return new ResponseEntity<>(new DataError(exception.getMessage()), HttpStatus.CONFLICT);
  }
}
