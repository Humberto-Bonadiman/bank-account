package com.java.spring.person;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class PersonFindAllApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  PersonRepository personRepository;

  @Autowired
  PersonService personService;

  @BeforeEach
  public void setup() {
    personRepository.deleteAll();
  }

  @Test
  @Order(1)
  @DisplayName("1 - Must show all registered people")
  void findAllPeopleSuccessfully() throws Exception {
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

    mockMvc.perform(get("/person").header("token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].cpf").value(firstPerson.getCpf()))
        .andExpect(jsonPath("$[0].fullName").value(firstPerson.getFullName()))
        .andExpect(jsonPath("$[1].cpf").value(secondPerson.getCpf()))
        .andExpect(jsonPath("$[1].fullName").value(secondPerson.getFullName()));
  }

  @Test
  @Order(2)
  @DisplayName("2 - throws a error if token is abscent")
  void throwsErrorWithoutToken() throws Exception {
    mockMvc.perform(get("/person"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(3)
  @DisplayName("3 - throws a error if token is invalid")
  void invalidToken() throws Exception {
    mockMvc.perform(get("/person").header("token", "abcd1525"))
      .andExpect(status().isUnauthorized());
  }
}
