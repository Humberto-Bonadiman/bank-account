package com.java.spring.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.java.spring.dto.PersonDto;
import com.java.spring.exception.CpfNotNumericException;
import com.java.spring.exception.DifferentIdException;
import com.java.spring.exception.IncorrectCpfLengthException;
import com.java.spring.exception.IncorrectFullNameLengthException;
import com.java.spring.exception.PersonAlreadyRegisteredException;
import com.java.spring.exception.PersonNotRegisteredException;
import com.java.spring.model.Person;
import com.java.spring.repository.PersonRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PersonService implements PersonInterface {

  @Autowired
  PersonRepository personRepository;

  @Override
  public Person create(PersonDto personDto) {
    try {
      checkIfPersonExistByCpf(personDto.getCpf());
      verifyFullNameLength(personDto.getFullName());
      verifyCpf(personDto.getCpf());
      Person person = new Person(personDto.getFullName(), personDto.getCpf());
      return personRepository.save(person);
    } catch (ResponseStatusException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "all values is required");
    }
  }

  @Override
  public String generateToken(PersonDto personDto) {
    verifyIfNull(personDto.getFullName(), personDto.getCpf());
    String secret = GlobalMethodsService.getSecret();
    Algorithm algorithm = Algorithm.HMAC256(secret);
    Person personFound = findByCpf(personDto.getCpf()).get();
    Map<String, Object> payloadClaims = new HashMap<>();
    payloadClaims.put("id", personFound.getId());
    payloadClaims.put("fullName", personFound.getFullName());
    payloadClaims.put("cpf", personFound.getCpf());
    String token = JWT.create()
        .withPayload(payloadClaims)
        .withExpiresAt(localDateNowMoreSeven())
        .sign(algorithm);
    return token;
  }

  @Override
  public List<Person> findAll(String token) {
    try {
      GlobalMethodsService.verifyToken(token);
      return personRepository.findAll();
    } catch (JWTVerificationException exception) {
      throw new JWTVerificationException("Expired or invalid token");
    }
  }

  @Override
  public void deleteById(String token, Long id) {
    DecodedJWT decoded = GlobalMethodsService.verifyToken(token);
    Person person = findById(id);
    Long numberId = GlobalMethodsService.returnIdToken(decoded);
    sameIds(numberId, id);
    personRepository.deleteById(person.getId());
  }

  /**
   * Add more seven days for date.
   */
  private Date localDateNowMoreSeven() {
    LocalDate todayMoreSeven =  LocalDate.now().plusDays(7);
    Date date = Date.from(todayMoreSeven.atStartOfDay(ZoneId.systemDefault()).toInstant());
    return date;
  }  

  private Optional<Person> checkIfPersonExistByCpf(String cpf) {
    Optional<Person> findPerson = personRepository.findByCpf(cpf);
    if (findPerson.isPresent()) {
      throw new PersonAlreadyRegisteredException();
    }
    return findPerson;
  }

  private Optional<Person> findByCpf(String cpf) {
    Optional<Person> findPerson = personRepository.findByCpf(cpf);
    if (findPerson.isEmpty()) {
      throw new PersonNotRegisteredException();
    }
    return findPerson;
  }

  private Person findById(Long id) {
    Optional<Person> validPerson = personRepository.findById(id);
    if (validPerson.isEmpty()) {
      throw new PersonNotRegisteredException();
    }
    return validPerson.get();
  }

  private void verifyFullNameLength(String fullName) {
    if (fullName.length() < 7) {
      throw new IncorrectFullNameLengthException();
    }
  }

  private void verifyCpf(String cpf) {
    if (cpf.length() != 11) {
      throw new IncorrectCpfLengthException();
    }
    if (!StringUtils.isNumeric(cpf)) {
      throw new CpfNotNumericException();
    }
  }

  private void verifyIfNull(String fullName, String cpf) {
    if (fullName == null || cpf == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "all values is required");
    }
  }

  private void sameIds(Long idToken, Long idGiven) {
    if (!idToken.equals(idGiven)) {
      throw new DifferentIdException();
    }
  }
}
