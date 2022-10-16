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
class AccountTransferApplicationTests {

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
  @DisplayName("1 - transfer successfully")
  void transferSucessfully() throws Exception {
    Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account account = new Account();
    account.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    account.setPasswordAccount(pw_hash);
    LocalDate localDate = global.convertDate("15/08/1990");
    account.setBirthDate(localDate);
    account.setCountry("Brasil");
    account.setState("Rio Grande do Sul");
    account.setCity("Porto Alegre");
    account.setStreet("Avenida Protássio Alves");
    account.setDistrict("Petrópolis");
    account.setPhoneNumber("(51) 99134-5678");
    account.setAccountBalance(0);
    account.setPerson(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    accountRepository.save(account);
    accountService.alterBalanceByAccountId(token, account.getId(), valueDto);

    Person secondPerson = new Person();
    secondPerson.setCpf("12345678905");
    secondPerson.setFullName("Gustavo Teste Almeida");
    personRepository.save(secondPerson);
    Account secondAccount = new Account();
    secondAccount.setEmail("gustavo_teste@email.com");
    String pwHash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    secondAccount.setPasswordAccount(pwHash);
    LocalDate localDateSecond = global.convertDate("15/08/1990");
    secondAccount.setBirthDate(localDateSecond);
    secondAccount.setCountry("Brasil");
    secondAccount.setState("Rio Grande do Sul");
    secondAccount.setCity("Porto Alegre");
    secondAccount.setStreet("Avenida Protássio Alves");
    secondAccount.setDistrict("Petrópolis");
    secondAccount.setPhoneNumber("(51) 99134-5678");
    secondAccount.setAccountBalance(0);
    secondAccount.setPerson(secondPerson);
    accountRepository.save(secondAccount);
    ValueDto transfer = new ValueDto();
    transfer.setPassword("12345678");
    transfer.setValue(1000);
    mockMvc
        .perform(patch("/account/" + account.getId() + "/to/" + secondAccount.getId())
    	.header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(transfer)))
        .andExpect(status().isOk());
  }

  @Test
  @Order(2)
  @DisplayName("2 - cannot transfer a negative value")
  void cannotTransferNegativeValue() throws Exception {
    ValueDto transfer = new ValueDto();
    transfer.setPassword("12345678");
    transfer.setValue(-1000);
    mockMvc
        .perform(patch("/account/1b3uv03/to/1bfiel3l9c")
    	.header("token", "tokenValue")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(transfer)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(3)
  @DisplayName("3 - throw an error if token is abscent")
  void throwErrorWithoutToken() throws Exception {
    ValueDto transfer = new ValueDto();
    transfer.setPassword("12345678");
    transfer.setValue(1000);
    mockMvc
        .perform(patch("/account/1b3uv03/to/1bfiel3l9c")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(transfer)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(4)
  @DisplayName("4 - throw an error if token is invalid")
  void invalidToken() throws Exception {
    ValueDto transfer = new ValueDto();
    transfer.setPassword("12345678");
    transfer.setValue(1000);
    mockMvc
        .perform(patch("/account/1b3uv03/to/1bfiel3l9c")
    	.header("token", "tokenValue")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(transfer)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(5)
  @DisplayName("5 - throw a error if ids are invalid")
  void invalidIds() throws Exception {
    Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    ValueDto transfer = new ValueDto();
    transfer.setPassword("12345678");
    transfer.setValue(1000);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc
        .perform(patch("/account/1b3uv03/to/1bfiel3l9c")
    	.header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(transfer)))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(6)
  @DisplayName("6 - throw an error with wrong password")
  void wrongPassword() throws Exception {
    Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account account = new Account();
    account.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    account.setPasswordAccount(pw_hash);
    LocalDate localDate = global.convertDate("15/08/1990");
    account.setBirthDate(localDate);
    account.setCountry("Brasil");
    account.setState("Rio Grande do Sul");
    account.setCity("Porto Alegre");
    account.setStreet("Avenida Protássio Alves");
    account.setDistrict("Petrópolis");
    account.setPhoneNumber("(51) 99134-5678");
    account.setAccountBalance(0);
    account.setPerson(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    accountRepository.save(account);
    accountService.alterBalanceByAccountId(token, account.getId(), valueDto);

    Person secondPerson = new Person();
    secondPerson.setCpf("12345678905");
    secondPerson.setFullName("Gustavo Teste Almeida");
    personRepository.save(secondPerson);
    Account secondAccount = new Account();
    secondAccount.setEmail("gustavo_teste@email.com");
    String pwHash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    secondAccount.setPasswordAccount(pwHash);
    LocalDate localDateSecond = global.convertDate("15/08/1990");
    secondAccount.setBirthDate(localDateSecond);
    secondAccount.setCountry("Brasil");
    secondAccount.setState("Rio Grande do Sul");
    secondAccount.setCity("Porto Alegre");
    secondAccount.setStreet("Avenida Protássio Alves");
    secondAccount.setDistrict("Petrópolis");
    secondAccount.setPhoneNumber("(51) 99134-5678");
    secondAccount.setAccountBalance(0);
    secondAccount.setPerson(secondPerson);
    accountRepository.save(secondAccount);
    ValueDto transfer = new ValueDto();
    transfer.setPassword("12345678abcd214");
    transfer.setValue(1000);
    mockMvc
        .perform(patch("/account/" + account.getId() + "/to/" + secondAccount.getId())
    	.header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(transfer)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(7)
  @DisplayName("7 - insufficient balance")
  void insufficientBalance() throws Exception {
    Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    Account account = new Account();
    account.setEmail("julio_teste@email.com");
    String pw_hash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    account.setPasswordAccount(pw_hash);
    LocalDate localDate = global.convertDate("15/08/1990");
    account.setBirthDate(localDate);
    account.setCountry("Brasil");
    account.setState("Rio Grande do Sul");
    account.setCity("Porto Alegre");
    account.setStreet("Avenida Protássio Alves");
    account.setDistrict("Petrópolis");
    account.setPhoneNumber("(51) 99134-5678");
    account.setAccountBalance(0);
    account.setPerson(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    ValueDto valueDto = new ValueDto();
    valueDto.setPassword("12345678");
    valueDto.setValue(1500);
    accountRepository.save(account);
    accountService.alterBalanceByAccountId(token, account.getId(), valueDto);

    Person secondPerson = new Person();
    secondPerson.setCpf("12345678905");
    secondPerson.setFullName("Gustavo Teste Almeida");
    personRepository.save(secondPerson);
    Account secondAccount = new Account();
    secondAccount.setEmail("gustavo_teste@email.com");
    String pwHash = BCrypt.hashpw("12345678", BCrypt.gensalt());
    secondAccount.setPasswordAccount(pwHash);
    LocalDate localDateSecond = global.convertDate("15/08/1990");
    secondAccount.setBirthDate(localDateSecond);
    secondAccount.setCountry("Brasil");
    secondAccount.setState("Rio Grande do Sul");
    secondAccount.setCity("Porto Alegre");
    secondAccount.setStreet("Avenida Protássio Alves");
    secondAccount.setDistrict("Petrópolis");
    secondAccount.setPhoneNumber("(51) 99134-5678");
    secondAccount.setAccountBalance(0);
    secondAccount.setPerson(secondPerson);
    accountRepository.save(secondAccount);
    ValueDto transfer = new ValueDto();
    transfer.setPassword("12345678");
    transfer.setValue(5000);
    mockMvc
        .perform(patch("/account/" + account.getId() + "/to/" + secondAccount.getId())
    	.header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(transfer)))
        .andExpect(status().isUnauthorized());
  }
}
