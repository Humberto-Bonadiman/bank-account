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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class PersonService implements PersonInterface<PersonDto, Person> {

  @Autowired
  PersonRepository personRepository;

  GlobalMethodsService global = new GlobalMethodsService();

  @Override
  public Person create(PersonDto personDto) {
    try {
      Optional<Person> findPerson = personRepository.findByCpf(personDto.getCpf());
      if (findPerson.isPresent()) throw new PersonAlreadyRegisteredException();
      if (personDto.getFullName().length() < 7) throw new IncorrectFullNameLengthException();
      if (personDto.getCpf().length() != 11) throw new IncorrectCpfLengthException();
      if (!StringUtils.isNumeric(personDto.getCpf())) throw new CpfNotNumericException();
      Person person = new Person();
      person.setFullName(personDto.getFullName());
      person.setCpf(personDto.getCpf());
      return personRepository.save(person);
    } catch(NullPointerException e) {
      throw new NullPointerException("all values is required");
  	}
  }

  @Override
  public String generateToken(PersonDto personDto) {
    if (personDto.getFullName() == null || personDto.getCpf() == null) {
       throw new NullPointerException("all values is required");
    }
    Optional<Person> isCpf = personRepository.findByCpf(personDto.getCpf());
    if (isCpf.isEmpty()) throw new PersonNotRegisteredException();
    String secret = System.getenv("SECRET");
    if (secret == null) {
    	secret = "BH&2&@2f3%#6qPt5B";
    }
    Algorithm algorithm = Algorithm.HMAC256(secret);
    Map<String, Object> payloadClaims = new HashMap<>();
    Person personFound = personRepository.findFirstByCpf(personDto.getCpf());
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
      global.verifyToken(token);
      return personRepository.findAll();
    } catch (JWTVerificationException exception){
      throw new JWTVerificationException("Expired or invalid token");
    }
  }

  @Override
  public void deleteById(String token, Long id) {
    global.verifyToken(token);
    DecodedJWT decoded = global.verifyToken(token);
    Optional<Person> isValidPerson = personRepository.findById(id);
    if (isValidPerson.isEmpty()) throw new PersonNotRegisteredException();
    Person person = personRepository.findById(id).get();
    Long numberId = global.returnIdToken(decoded);
    if (!numberId.equals(id)) throw new DifferentIdException();
    personRepository.deleteById(person.getId());
  }

  public Date localDateNowMoreSeven() {
    LocalDate todayMoreSeven =  LocalDate.now().plusDays(7);
    Date date = Date.from(todayMoreSeven.atStartOfDay(ZoneId.systemDefault()).toInstant());
    return date;
  } 

}
