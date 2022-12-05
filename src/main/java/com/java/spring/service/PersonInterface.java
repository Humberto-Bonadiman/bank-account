package com.java.spring.service;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import java.util.List;

public interface PersonInterface {

  public Person create(PersonDto personDto);

  public String generateToken(PersonDto personDto);

  public List<Person> findAll(String token);

  public void deleteById(String token, Long id);

}
