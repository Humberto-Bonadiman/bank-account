package com.java.spring.service;

import com.java.spring.dto.PersonDto;
import com.java.spring.exception.IncorrectCpfLengthException;
import com.java.spring.exception.IncorrectFullNameLengthException;
import com.java.spring.exception.PersonAlreadyRegisteredException;
import com.java.spring.model.Person;
import com.java.spring.repository.PersonRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class PersonService implements PersonInterface<PersonDto, Person> {

  @Autowired
  PersonRepository personRepository;

  @Override
  public Person create(PersonDto personDto) {
    try {
      Optional<Person> findPerson = personRepository.findByCpf(personDto.getCpf());
      if (findPerson.isPresent()) throw new PersonAlreadyRegisteredException();
      if (personDto.getFullName().length() < 7) throw new IncorrectFullNameLengthException();
      if (personDto.getCpf().length() != 11) throw new IncorrectCpfLengthException();
      Person person = new Person();
      person.setFullName(personDto.getFullName());
      person.setCpf(personDto.getCpf());
      return personRepository.save(person);
    } catch(NullPointerException e) {
        throw new NullPointerException("all values is required");
  	}
  }

  @Override
  public Person generateToken(String fullName) {
    return null;
  }

  @Override
  public List<Person> findAll(String token) {
    return null;
  }

}
