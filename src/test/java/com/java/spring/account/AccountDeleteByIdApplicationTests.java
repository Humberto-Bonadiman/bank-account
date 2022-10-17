package com.java.spring.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.java.spring.dto.PasswordDto;
import com.java.spring.dto.PersonDto;
import com.java.spring.model.Account;
import com.java.spring.model.Person;
import com.java.spring.repository.AccountRepository;
import com.java.spring.repository.PersonRepository;
import com.java.spring.service.AccountService;
import com.java.spring.service.GlobalMethodsService;
import com.java.spring.service.PersonService;

@SpringBootTest
@AutoConfigureMockMvc
class AccountDeleteByIdApplicationTests {

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
  @DisplayName("1 - Delete an account successfully")
  void deleteAccount() throws Exception {
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
    accountRepository.save(account);
    String token = personService.generateToken(personDto);
    PasswordDto password = new PasswordDto("12345678");

    mockMvc.perform(delete("/account/" + account.getId())
	    .header("token", token)
	    .contentType(MediaType.APPLICATION_JSON)
	    .content(new ObjectMapper().writeValueAsString(password)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(2)
  @DisplayName("2 - throws an error if token is abscent")
  void throwsErrorWithoutToken() throws Exception {
    Account account = new Account();
    PasswordDto password = new PasswordDto("12345678");
    mockMvc.perform(delete("/account/" + account.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(password)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(3)
  @DisplayName("3 - throws an error if token is invalid")
  void invalidToken() throws Exception {
    Account account = new Account();
    PasswordDto password = new PasswordDto("12345678");
    mockMvc.perform(delete("/account/" + account.getId())
        .header("token", "abcd1525")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(password)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(4)
  @DisplayName("4 - throws an error if id of account is invalid")
  void invalidIdAccount() throws Exception {
    Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    PasswordDto password = new PasswordDto("12345678");
    mockMvc.perform(delete("/account/yiqzb4")
            .header("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(password)))
            .andExpect(status().isNotFound());
  }

  @Test
  @Order(5)
  @DisplayName("5 - throws an error with wrong password")
  void invalidPassword() throws Exception {
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
    accountRepository.save(account);
    String token = personService.generateToken(personDto);
    PasswordDto password = new PasswordDto("12345678abcd83jc");

    mockMvc.perform(delete("/account/" + account.getId())
        .header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(password)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(5)
  @DisplayName("5 - throws an error if password is not provided")
  void passwordNotProvided() throws Exception {
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
    accountRepository.save(account);
    String token = personService.generateToken(personDto);
    PasswordDto password = new PasswordDto();

    mockMvc.perform(delete("/account/" + account.getId())
        .header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(password)))
        .andExpect(status().isBadRequest());
  }
}
