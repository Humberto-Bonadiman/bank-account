package com.java.spring.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.java.spring.dto.AccountDto;
import com.java.spring.dto.ValueDto;
import com.java.spring.exception.AccountNotFoundException;
import com.java.spring.exception.IncorrectEmailFormat;
import com.java.spring.exception.PasswordLengthException;
import com.java.spring.exception.WithdrawGreaterThanBalanceException;
import com.java.spring.model.Account;
import com.java.spring.model.Person;
import com.java.spring.repository.AccountRepository;
import com.java.spring.repository.PersonRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
    checkIfIsNotNull(accountDto);
    global.verifyToken(token);
    if (!global.isValidEmailAddress(accountDto.getEmail())) {
      throw new IncorrectEmailFormat();
    }
    if (!global.isValidPasswordLength(accountDto.getPasswordAccount())) {
    	throw new PasswordLengthException();
    }
    DecodedJWT decoded = global.verifyToken(token);
    Long numberId = global.returnIdToken(decoded);
    Account newAccount = new Account();
    newAccount.setEmail(accountDto.getEmail());
    String pw_hash = BCrypt.hashpw(accountDto.getPasswordAccount(), BCrypt.gensalt());
    newAccount.setPasswordAccount(pw_hash);
    newAccount.setAccountBalance(0);
    LocalDate localDate = global.convertDate(accountDto.getBirthDate());
    newAccount.setBirthDate(localDate);
    newAccount.setCountry(accountDto.getCountry());
    newAccount.setState(accountDto.getState());
    newAccount.setCity(accountDto.getCity());
    newAccount.setStreet(accountDto.getStreet());
    newAccount.setDistrict(accountDto.getDistrict());
    newAccount.setPhoneNumber(accountDto.getPhoneNumber());
    Person person = personRepository.findById(numberId).get();
    newAccount.setPerson(person);
    person.setAccount(newAccount);
    Person savedPerson = personRepository.save(person);
    return savedPerson.getAccount();
  }

  @Override
  public Account findById(String token, String id) {
    global.verifyToken(token);
    Optional<Account> isValidId = accountRepository.findById(id);
    if (isValidId.isEmpty()) throw new AccountNotFoundException();
    return accountRepository.findById(id).get();
  }

  @Override
  public Account alterBalanceByAccountId(String token, String id, ValueDto value) {
    global.verifyToken(token);
    Optional<Account> isValidId = accountRepository.findById(id);
    if (isValidId.isEmpty()) throw new AccountNotFoundException();
    Account account = accountRepository.findById(id).get();
    System.out.println(account.getAccountBalance());
    Integer sumValues = account.getAccountBalance() + value.getValue();
    System.out.println(sumValues);
    if (sumValues < 0) {
      throw new WithdrawGreaterThanBalanceException();
    }
    account.setAccountBalance(sumValues);
    accountRepository.save(account);
    return account;
  }

  public void checkIfIsNotNull(AccountDto accountDto) {
    if (accountDto.getEmail() == null
        || accountDto.getPasswordAccount() == null
        || accountDto.getBirthDate() == null
        || accountDto.getCountry() == null
        || accountDto.getState() == null
        || accountDto.getCity() == null
        || accountDto.getStreet() == null
        || accountDto.getDistrict() == null
        || accountDto.getPhoneNumber() == null) {
      String firstPart = "the values email, passwordAccount, birthDate, country, state";
      String secondPart = ", city, street, district, phoneNumber, personId";
      throw new NullPointerException(firstPart + secondPart + " are required");
    }
  }
}
