package com.java.spring.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.java.spring.exception.DatetimeConvertionException;
import com.java.spring.exception.TokenNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
// import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.EmailValidator;

public class GlobalMethodsService {

  public DecodedJWT verifyToken(String token) {
    try {
      if (token.equals("")) throw new TokenNotFoundException();
      String secret = System.getenv("SECRET");
      if (secret == null) {
      	secret = "BH&2&@2f3%#6qPt5B";
      }
      Algorithm algorithm = Algorithm.HMAC256(secret);  
      JWTVerifier verifier = JWT.require(algorithm).build();
      return verifier.verify(token);
    } catch (JWTVerificationException e) {
      throw new JWTVerificationException("Expired or invalid token");
    }

  }

  public Long returnIdToken(DecodedJWT decoded) {
    String encPayload = decoded.getPayload();
    String payload = decode(encPayload);
    String firstPartPayload = payload.substring(payload.indexOf("id"));
    Integer colon  = firstPartPayload.indexOf(":");
    String secondPartPayload = firstPartPayload.substring(colon + 1);
    return Long.parseLong(
        secondPartPayload.substring(0, secondPartPayload.indexOf(","))); 
  }

  public String decode(final String base64) {
    return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
  }

  public LocalDate convertDate(String date) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
      LocalDate localDate = LocalDate.parse(date, formatter);
      return localDate;
    } catch (DateTimeParseException e) {
      throw new DatetimeConvertionException();
    }
  }

  public boolean isValidEmailAddress(String email) {
    boolean valid = EmailValidator.getInstance().isValid(email);
    return valid;
  }

  public boolean isValidPasswordLength(String password) {
    if(password.length() >= 6) return true;
    return false;
  }

  public boolean isValidFullNameLength(String fullName) {
    if (fullName.length() >= 8) return true;
    return false;
  }
}
