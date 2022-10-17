package com.java.spring.person;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import com.java.spring.repository.PersonRepository;
import com.java.spring.service.PersonService;

@SpringBootTest
@AutoConfigureMockMvc
class PersonDeleteByIdApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  PersonRepository personRepository;

  @Autowired
  PersonService personService;

  @BeforeEach
  void setUp() throws Exception {
    personRepository.deleteAll();
  }

  @Test
  @Order(1)
  @DisplayName("1 - Must delete a person by id successfully")
  void deleteById() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc.perform(delete("/person/" + person.getId()).header("token", token))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(2)
  @DisplayName("2 - throws an error if token is abscent")
  void throwsErrorWithoutToken() throws Exception {
    mockMvc.perform(delete("/person/10"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(3)
  @DisplayName("3 - throws an error if token is invalid")
  void invalidToken() throws Exception {
    mockMvc.perform(delete("/person/10").header("token", "abcd1525"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(4)
  @DisplayName("4 - throws an error if id is invalid")
  void invalidId() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    Long wrongId = person.getId() + 10L;
    mockMvc.perform(delete("/person/" + wrongId).header("token", token))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(5)
  @DisplayName("5 - throws an error if the token does not belong to the given id")
  void idAndTokenDoNotMatch() throws Exception {
    Person firstPerson = new Person();
    firstPerson.setCpf("12345678905");
    firstPerson.setFullName("Júlio Teste da Silva");
    personRepository.save(firstPerson);
    Person secondPerson = new Person();
    secondPerson.setCpf("09876543215");
    secondPerson.setFullName("Gabriela Teste Alencar");
    personRepository.save(secondPerson);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(firstPerson.getCpf());
    personDto.setFullName(firstPerson.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc.perform(delete("/person/" + secondPerson.getId()).header("token", token))
        .andExpect(status().isUnauthorized());
  }
}
