package com.java.spring.person;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import com.java.spring.repository.PersonRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PersonTokenApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PersonRepository personRepository;

  @BeforeEach
  public void setup() {
    personRepository.deleteAll();
  }

  @Test
  @Order(1)
  @DisplayName("1 - Must generate a token successfully")
  void generateTokenSuccesfully() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    mockMvc.perform(post("/person/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(personDto)))
            .andExpect(status().isOk())
            .andReturn();
  }

  @Test
  @Order(2)
  @DisplayName("2 - Cannot generate a token without cpf or fullName")
  void failWithoutCpfOrFullName() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    PersonDto firstPersonDto = new PersonDto();
    firstPersonDto.setCpf(person.getCpf());
    PersonDto secondPersonDto = new PersonDto();
    secondPersonDto.setFullName(person.getFullName());

    mockMvc.perform(post("/person/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(firstPersonDto)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/person/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(secondPersonDto)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(3)
  @DisplayName("3 - Cannot generate a token if the person is not registered")
  void failWithoutPersonRegister() throws Exception {
    PersonDto person = new PersonDto();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");

    mockMvc.perform(post("/person/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(person)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
