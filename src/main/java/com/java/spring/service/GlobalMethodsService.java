package com.java.spring.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.java.spring.exception.TokenNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
// import org.springframework.stereotype.Service;

public class GlobalMethodsService {

  public DecodedJWT verifyToken(String token) {
    if (token.equals("")) throw new TokenNotFoundException();
    Algorithm algorithm = Algorithm.HMAC256(System.getenv("SECRET"));  
    JWTVerifier verifier = JWT.require(algorithm).build();
    return verifier.verify(token);
  }

  public Long returnIdToken(DecodedJWT decoded) {
    String encPayload = decoded.getPayload();
    String payload = decode(encPayload);
    String firstPartPayload = payload.substring(payload.indexOf("id") + 4);
    return Long.parseLong(
        firstPartPayload.substring(0, firstPartPayload.indexOf(","))); 
  }

  public String decode(final String base64) {
    return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
  }
}
