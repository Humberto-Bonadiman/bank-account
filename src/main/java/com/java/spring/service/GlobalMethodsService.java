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
import org.apache.commons.validator.routines.EmailValidator;

public class GlobalMethodsService {

  private static final String secretDefault = "BH&2&@2f3%#6qPt5B";

  /**
   * verify if is a valid token.
   */
  public static DecodedJWT verifyToken(String token) {
    try {
      if ("".equals(token)) {
        throw new TokenNotFoundException();
      }
      String secret = getSecret();
      Algorithm algorithm = Algorithm.HMAC256(secret);  
      JWTVerifier verifier = JWT.require(algorithm).build();
      return verifier.verify(token);
    } catch (JWTVerificationException e) {
      throw new JWTVerificationException("Expired or invalid token");
    }

  }

  /**
   * get the id in token.
   */
  public static Long returnIdToken(DecodedJWT decoded) {
    String encPayload = decoded.getPayload();
    String payload = decode(encPayload);
    String firstPartPayload = payload.substring(payload.indexOf("id"));
    Integer colon  = firstPartPayload.indexOf(":");
    String secondPartPayload = firstPartPayload.substring(colon + 1);
    return Long.parseLong(
        secondPartPayload.substring(0, secondPartPayload.indexOf(","))); 
  }

  public static String decode(final String base64) {
    return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
  }

  /**
   * convert string to LocalDate.
   */
  public static LocalDate convertDate(String date) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
      LocalDate localDate = LocalDate.parse(date, formatter);
      return localDate;
    } catch (DateTimeParseException e) {
      throw new DatetimeConvertionException();
    }
  }

  /**
   * get secret.
   */
  public static String getSecret() {
    String secret = System.getenv("SECRET");
    if (secret == null) {
      secret = secretDefault;
    }
    return secret;
  }

  /**
   * check if is a valid email.
   */
  public static boolean isValidEmailAddress(String email) {
    return EmailValidator.getInstance().isValid(email);
  }

  /**
   * check the length of password.
   */
  public static boolean isValidPasswordLength(String password) {
    return password.length() >= 6;
  }

  /**
   * check the length of fullName.
   */
  public boolean isValidFullNameLength(String fullName) {
    return fullName.length() >= 8;
  }
}
