package com.java.spring;

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

import com.java.spring.model.Person;
import com.java.spring.repository.PersonRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PersonApplicationTests {

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
  @DisplayName("1 - Must register a person successfully")
  void registerPersonSuccessfully() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    mockMvc.perform(post("/person")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(person)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  @DisplayName("2 - It is not possible to register two identical cpfs")
  void failToRegisterTwoEqualsCpfs() throws Exception {
    final Person firstPerson = new Person();
    firstPerson.setCpf("12345678901");
    firstPerson.setFullName("Júlio Teste da Silva");
    final Person secondPerson = new Person();
    secondPerson.setFullName("Ricardo Oliveira Teste");
    secondPerson.setCpf("12345678901");

    mockMvc.perform(post("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(firstPerson)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/person")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(secondPerson)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
  }

  @Test
  @Order(3)
  @DisplayName("3 - cannot register when fullName length is less than 7")
  void failToRegisterByFullNameLength() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Teste1");

    mockMvc.perform(post("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(person)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(4)
  @DisplayName("4 - cannot register when cpf length is different from 11")
  void failToRegisterByCpfLength() throws Exception {
    final Person firstPerson = new Person();
    firstPerson.setCpf("1234567890");
    firstPerson.setFullName("Júlio Teste da Silva");
    final Person secondPerson = new Person();
    secondPerson.setFullName("Ricardo Oliveira Teste");
    secondPerson.setCpf("123456789012");

    mockMvc.perform(post("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(firstPerson)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(secondPerson)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(5)
  @DisplayName("5 - cannot register if cpf or fullName are not present")
  void allValuesAreRequired() throws Exception {
    final Person firstPerson = new Person();
    firstPerson.setCpf("12345678901");
    final Person secondPerson = new Person();
    secondPerson.setFullName("Ricardo Oliveira Teste");

    mockMvc.perform(post("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(firstPerson)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(secondPerson)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(6)
  @DisplayName("6 - cannot register if cpf is not numeric")
  void cpfNotNumeric() throws Exception {
    final Person person = new Person();
    person.setCpf("12t4e678a01");
    person.setFullName("Júlio Teste da Silva");

    mockMvc.perform(post("/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(person)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
