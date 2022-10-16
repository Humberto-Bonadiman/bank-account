package com.java.spring.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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
import com.java.spring.model.Account;
import com.java.spring.model.Person;
import com.java.spring.repository.AccountRepository;
import com.java.spring.repository.PersonRepository;
import com.java.spring.service.GlobalMethodsService;
import com.java.spring.service.PersonService;

@SpringBootTest
@AutoConfigureMockMvc
class AccountFindByIdApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  PersonRepository personRepository;

  @Autowired
  PersonService personService;

  GlobalMethodsService global = new GlobalMethodsService();

  @BeforeEach
  void setUp() throws Exception {
    personRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  @Order(1)
  @DisplayName("1 - Find an account by id")
  void findAccountById() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account newAccount = new Account();
    newAccount.setEmail("julio_teste@email.com");
    newAccount.setPasswordAccount("12345678");
    LocalDate localDate = global.convertDate("15/08/1990");
    newAccount.setBirthDate(localDate);
    newAccount.setCountry("Brasil");
    newAccount.setState("Rio Grande do Sul");
    newAccount.setCity("Porto Alegre");
    newAccount.setStreet("Avenida Protássio Alves");
    newAccount.setDistrict("Petrópolis");
    newAccount.setPhoneNumber("(51) 99134-5678");
    newAccount.setAccountBalance(0);
    newAccount.setPerson(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    accountRepository.save(newAccount);
    mockMvc.perform(get("/account/" + newAccount.getId()).header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(newAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @Order(2)
  @DisplayName("2 - throw an error if token is abscent")
  void throwErrorWithoutToken() throws Exception {
    Account account = new Account();
    mockMvc.perform(get("/account/" + account.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(account)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(3)
  @DisplayName("3 - throw an error if token is invalid")
  void invalidToken() throws Exception {
    Account account = new Account();
    mockMvc.perform(get("/account/" + account.getId()).header("token", "abcd1525")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(account)))
        .andExpect(status().isUnauthorized());
  }

  
  @Test
  @Order(4)
  @DisplayName("4 - throw an error if id not exist")
  void idNotFound() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account newAccount = new Account();
    newAccount.setEmail("julio_teste@email.com");
    newAccount.setPasswordAccount("12345678");
    LocalDate localDate = global.convertDate("15/08/1990");
    newAccount.setBirthDate(localDate);
    newAccount.setCountry("Brasil");
    newAccount.setState("Rio Grande do Sul");
    newAccount.setCity("Porto Alegre");
    newAccount.setStreet("Avenida Protássio Alves");
    newAccount.setDistrict("Petrópolis");
    newAccount.setPhoneNumber("(51) 99134-5678");
    newAccount.setAccountBalance(0);
    newAccount.setPerson(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    accountRepository.save(newAccount);
    mockMvc.perform(get("/account/12g3nv8").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(newAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
