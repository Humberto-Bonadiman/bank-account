package com.java.spring.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.java.spring.dto.AccountDto;
import com.java.spring.model.Account;
import com.java.spring.model.Person;
import com.java.spring.repository.AccountRepository;
import com.java.spring.repository.PersonRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class AccountService implements AccountInterface<AccountDto, Account> {

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  PersonRepository personRepository;

  GlobalMethodsService global = new GlobalMethodsService();

  @Override
  public Account create(AccountDto accountDto, String token) {
	try {
      global.verifyToken(token);
      Account newAccount = new Account();
      newAccount.setEmail(accountDto.getEmail());
      newAccount.setPasswordAccount(accountDto.getPasswordAccount());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
      LocalDate localDate = LocalDate.parse(accountDto.getBirthDate(), formatter);
      newAccount.setBirthDate(localDate);
      newAccount.setCountry(accountDto.getCountry());
      newAccount.setState(accountDto.getState());
      newAccount.setCity(accountDto.getCity());
      newAccount.setStreet(accountDto.getStreet());
      newAccount.setDistrict(accountDto.getDistrict());
      newAccount.setPhoneNumber(accountDto.getPhoneNumber());
      Person person = personRepository.findById(accountDto.getPersonId()).get();
      newAccount.setPerson(person);
      person.setAccount(newAccount);
      Person savedPerson = personRepository.save(person);
      return savedPerson.getAccount();
	} catch (JWTVerificationException exception){
      throw new JWTVerificationException("Expired or invalid token");
    }
  }

}
