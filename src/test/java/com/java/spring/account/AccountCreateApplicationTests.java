package com.java.spring.account;

import com.java.spring.dto.AccountDto;
import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import com.java.spring.repository.AccountRepository;
import com.java.spring.repository.PersonRepository;
import com.java.spring.service.GlobalMethodsService;
import com.java.spring.service.PersonService;

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

@SpringBootTest
@AutoConfigureMockMvc
class AccountCreateApplicationTests {

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
  @DisplayName("1 - Must register an account successfully")
  void createAccountSuccessfully() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    AccountDto newAccount = new AccountDto();
    newAccount.setEmail("julio_teste@email.com");
    newAccount.setPasswordAccount("12345678");
    newAccount.setBirthDate("15/08/1990");
    newAccount.setCountry("Brasil");
    newAccount.setState("Rio Grande do Sul");
    newAccount.setCity("Porto Alegre");
    newAccount.setStreet("Avenida Protássio Alves");
    newAccount.setDistrict("Petrópolis");
    newAccount.setPhoneNumber("(51) 99134-5678");
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc.perform(post("/account").header("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(newAccount)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  @DisplayName("2 - throw an error if token is abscent")
  void throwErrorWithoutToken() throws Exception {
    AccountDto account = new AccountDto();
    mockMvc.perform(post("/account")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(account)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(3)
  @DisplayName("3 - throw an error if token is invalid")
  void invalidToken() throws Exception {
    AccountDto account = new AccountDto();
    mockMvc.perform(post("/account").header("token", "abcd1525")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(account)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Order(4)
  @DisplayName("4 - throws an error if the email is in the wrong format")
  void wrongFormatEmail() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    AccountDto newAccount = new AccountDto();
    newAccount.setEmail("@email.com");
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(newAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto secondAccount = new AccountDto();
    secondAccount.setEmail("julio_testeemail.com");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(secondAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto thirdAccount = new AccountDto();
    thirdAccount.setEmail("julio_teste@");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(thirdAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(4)
  @DisplayName("4 - the password is not of the proper length")
  void passwordLengthError() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    AccountDto newAccount = new AccountDto();
    newAccount.setPasswordAccount("12345");
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(newAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(5)
  @DisplayName("5 - birthDate is in wrong format")
  void wrongBirthDateFormat() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    AccountDto newAccount = new AccountDto();
    newAccount.setBirthDate("10/10/95");
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(newAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto secondAccount = new AccountDto();
    secondAccount.setBirthDate("10-10-1995");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(secondAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto thirdAccount = new AccountDto();
    thirdAccount.setBirthDate("10/30/1995");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(thirdAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto fourthAccount = new AccountDto();
    fourthAccount.setBirthDate("/10/1995");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(fourthAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(6)
  @DisplayName("6 - all values are required")
  void allValuesRequired() throws Exception {
    final Person person = new Person();
    person.setCpf("12345678901");
    person.setFullName("Júlio Teste da Silva");
    personRepository.save(person);
    AccountDto newAccount = new AccountDto();
    newAccount.setPasswordAccount("12345678");
    newAccount.setBirthDate("15/08/1990");
    newAccount.setCountry("Brasil");
    newAccount.setState("Rio Grande do Sul");
    newAccount.setCity("Porto Alegre");
    newAccount.setStreet("Avenida Protássio Alves");
    newAccount.setDistrict("Petrópolis");
    newAccount.setPhoneNumber("(51) 99134-5678");
    PersonDto personDto = new PersonDto();
    personDto.setCpf(person.getCpf());
    personDto.setFullName(person.getFullName());
    String token = personService.generateToken(personDto);
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(newAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto secondAccount = new AccountDto();
    secondAccount.setEmail("julio_teste@email.com");
    secondAccount.setBirthDate("15/08/1990");
    secondAccount.setCountry("Brasil");
    secondAccount.setState("Rio Grande do Sul");
    secondAccount.setCity("Porto Alegre");
    secondAccount.setStreet("Avenida Protássio Alves");
    secondAccount.setDistrict("Petrópolis");
    secondAccount.setPhoneNumber("(51) 99134-5678");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(secondAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto thirdAccount = new AccountDto();
    thirdAccount.setEmail("julio_teste@email.com");
    thirdAccount.setPasswordAccount("12345678");
    thirdAccount.setCountry("Brasil");
    thirdAccount.setState("Rio Grande do Sul");
    thirdAccount.setCity("Porto Alegre");
    thirdAccount.setStreet("Avenida Protássio Alves");
    thirdAccount.setDistrict("Petrópolis");
    thirdAccount.setPhoneNumber("(51) 99134-5678");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(thirdAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto fourthAccount = new AccountDto();
    fourthAccount.setEmail("julio_teste@email.com");
    fourthAccount.setPasswordAccount("12345678");
    fourthAccount.setBirthDate("15/08/1990");
    fourthAccount.setState("Rio Grande do Sul");
    fourthAccount.setCity("Porto Alegre");
    fourthAccount.setStreet("Avenida Protássio Alves");
    fourthAccount.setDistrict("Petrópolis");
    fourthAccount.setPhoneNumber("(51) 99134-5678");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(fourthAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto fifthAccount = new AccountDto();
    fifthAccount.setEmail("julio_teste@email.com");
    fifthAccount.setPasswordAccount("12345678");
    fifthAccount.setBirthDate("15/08/1990");
    fifthAccount.setCountry("Brasil");
    fifthAccount.setCity("Porto Alegre");
    fifthAccount.setStreet("Avenida Protássio Alves");
    fifthAccount.setDistrict("Petrópolis");
    fifthAccount.setPhoneNumber("(51) 99134-5678");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(fifthAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto sixthAccount = new AccountDto();
    sixthAccount.setEmail("julio_teste@email.com");
    sixthAccount.setPasswordAccount("12345678");
    sixthAccount.setBirthDate("15/08/1990");
    sixthAccount.setCountry("Brasil");
    sixthAccount.setState("Rio Grande do Sul");
    sixthAccount.setStreet("Avenida Protássio Alves");
    sixthAccount.setDistrict("Petrópolis");
    sixthAccount.setPhoneNumber("(51) 99134-5678");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(sixthAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto seventhAccount = new AccountDto();
    seventhAccount.setEmail("julio_teste@email.com");
    seventhAccount.setPasswordAccount("12345678");
    seventhAccount.setBirthDate("15/08/1990");
    seventhAccount.setCountry("Brasil");
    seventhAccount.setState("Rio Grande do Sul");
    seventhAccount.setCity("Porto Alegre");
    seventhAccount.setDistrict("Petrópolis");
    seventhAccount.setPhoneNumber("(51) 99134-5678");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(seventhAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto eighthAccount = new AccountDto();
    eighthAccount.setEmail("julio_teste@email.com");
    eighthAccount.setPasswordAccount("12345678");
    eighthAccount.setBirthDate("15/08/1990");
    eighthAccount.setCountry("Brasil");
    eighthAccount.setState("Rio Grande do Sul");
    eighthAccount.setCity("Porto Alegre");
    eighthAccount.setStreet("Avenida Protássio Alves");
    eighthAccount.setPhoneNumber("(51) 99134-5678");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(eighthAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    AccountDto ninthAccount = new AccountDto();
    ninthAccount.setEmail("julio_teste@email.com");
    ninthAccount.setPasswordAccount("12345678");
    ninthAccount.setBirthDate("15/08/1990");
    ninthAccount.setCountry("Brasil");
    ninthAccount.setState("Rio Grande do Sul");
    ninthAccount.setCity("Porto Alegre");
    ninthAccount.setStreet("Avenida Protássio Alves");
    ninthAccount.setDistrict("Petrópolis");
    mockMvc.perform(post("/account").header("token", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(ninthAccount)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
