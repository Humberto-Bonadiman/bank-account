package com.java.spring.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.java.spring.dto.PersonDto;
import com.java.spring.dto.ValueDto;
import com.java.spring.model.Account;
import com.java.spring.model.Person;
import com.java.spring.repository.AccountRepository;
import com.java.spring.repository.PersonRepository;
import com.java.spring.service.AccountService;
import com.java.spring.service.GlobalMethodsService;
import com.java.spring.service.PersonService;

@SpringBootTest
@AutoConfigureMockMvc
class AccountAlterBalanceApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  PersonRepository personRepository;

  @Autowired
  PersonService personService;

  @Autowired
  AccountService accountService;

  GlobalMethodsService global = new GlobalMethodsService();

  @BeforeEach
  void setUp() throws Exception {
    personRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  @Order(1)
  @DisplayName("1 - make a successful deposit")
  void depositSuccessfuly() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account newAccount = new Account();
    newAccount.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    newAccount.setPasswordAccount(pw_hash);
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
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    accountRepository.save(newAccount);

    mockMvc.perform(patch("/account/balance/" + newAccount.getId()).header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(valueDto)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
  
  @Test
  @Order(2)
  @DisplayName("2 - throw an error if token is abscent")
  void throwErrorWithoutToken() throws Exception {
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    mockMvc.perform(patch("/account/balance/580fjc")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(valueDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(3)
  @DisplayName("3 - throw an error if token is invalid")
  void invalidToken() throws Exception {
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    mockMvc.perform(patch("/account/balance/580fjc").header("token", "abcd1525")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(valueDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(4)
  @DisplayName("4 - throw an error if id is invalid")
  void invalidId() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    mockMvc.perform(patch("/account/balance/580fjc").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(valueDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(5)
  @DisplayName("5 - throw an error if password is invalid")
  void invalidPassword() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account newAccount = new Account();
    newAccount.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678abc", BCrypt.gensalt());
    newAccount.setPasswordAccount(pw_hash);
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
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    accountRepository.save(newAccount);

    mockMvc.perform(patch("/account/balance/" + newAccount.getId()).header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(valueDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(5)
  @DisplayName("5 - throw an error if value greater than 2000")
  void valueGreaterThanAllowed() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account newAccount = new Account();
    newAccount.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    newAccount.setPasswordAccount(pw_hash);
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
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(2001);
    accountRepository.save(newAccount);

    mockMvc.perform(patch("/account/balance/" + newAccount.getId()).header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(valueDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(6)
  @DisplayName("6 - withdrawal amount is greater than the deposited")
  void withdrawalGreaterThanDeposited() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account newAccount = new Account();
    newAccount.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    newAccount.setPasswordAccount(pw_hash);
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
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    accountRepository.save(newAccount);
    accountService.alterBalanceByAccountId(token, newAccount.getId(), valueDto);
    ValueDto withdraw = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(-1600);

    mockMvc.perform(patch("/account/balance/" + newAccount.getId()).header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(withdraw)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(7)
  @DisplayName("7 - all values are required")
  void allValuesRequired() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account newAccount = new Account();
    newAccount.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    newAccount.setPasswordAccount(pw_hash);
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
    ValueDto firstvalue = new ValueDto();
    firstvalue.setPassword("12345678");
    ValueDto secondValue = new ValueDto();
    secondValue.setValue(1000);
    accountRepository.save(newAccount);

    mockMvc.perform(patch("/account/balance/" + newAccount.getId()).header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(firstvalue)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    mockMvc.perform(patch("/account/balance/" + newAccount.getId()).header("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(secondValue)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }
}
