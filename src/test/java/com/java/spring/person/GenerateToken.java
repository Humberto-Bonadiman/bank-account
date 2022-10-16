package com.java.spring.person;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.java.spring.model.Person;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GenerateToken {

  Algorithm algorithm = Algorithm.HMAC256("KAm12e6@1ADX");

  public String generate(Person person) {
    Map<String, Object> payloadClaims = new HashMap<>();
    payloadClaims.put("id", person.getId());
    payloadClaims.put("fullName", person.getFullName());
    payloadClaims.put("cpf", person.getCpf());
    String token = JWT.create()
        .withPayload(payloadClaims)
        .withExpiresAt(localDateNowMoreSeven())
        .sign(algorithm);
    return token;
  }

  public Date localDateNowMoreSeven() {
    LocalDate todayMoreSeven =  LocalDate.now().plusDays(7);
    Date date = Date.from(todayMoreSeven.atStartOfDay(ZoneId.systemDefault()).toInstant());
    return date;
  } 
}
