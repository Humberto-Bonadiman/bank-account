package com.java.spring.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.java.spring.dto.AccountDto;
import com.java.spring.dto.PasswordDto;
import com.java.spring.dto.ValueDto;
import com.java.spring.exception.AccountNotFoundException;
import com.java.spring.exception.IncorrectPasswordException;
import com.java.spring.exception.InsufficientBalanceException;
import com.java.spring.exception.NegativeValueException;
import com.java.spring.exception.ValueDepositException;
import com.java.spring.exception.WithdrawGreaterThanBalanceException;
import com.java.spring.model.Account;
import com.java.spring.model.Person;
import com.java.spring.repository.AccountRepository;
import com.java.spring.repository.PersonRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService implements AccountInterface<AccountDto, Account> {

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  PersonRepository personRepository;

  @Override
  public Account create(AccountDto accountDto, String token) {
    DecodedJWT decoded = GlobalMethodsService.verifyToken(token);
    GlobalMethodsService.isValidEmailAddress(accountDto.getEmail());
    GlobalMethodsService.isValidPasswordLength(accountDto.getPasswordAccount());
    Long numberId = GlobalMethodsService.returnIdToken(decoded);
    String pwHash = BCrypt.hashpw(accountDto.getPasswordAccount(), BCrypt.gensalt());
    LocalDate localDate = GlobalMethodsService.convertDate(accountDto.getBirthDate());
    Person person = personRepository.findById(numberId).get();
    Account newAccount = new Account();
    newAccount.buildAccount(accountDto, pwHash, localDate, person);
    person.setAccount(newAccount);
    checkIfIsNotNull(accountDto);
    Person savedPerson = personRepository.save(person);
    return savedPerson.getAccount();
  }

  @Override
  public Account findById(String token, String id) {
    GlobalMethodsService.verifyToken(token);
    return findByIdOrThrowError(id);
  }

  @Override
  public Account alterBalanceByAccountId(
      String token,
      String id,
      ValueDto value
  ) {
    GlobalMethodsService.verifyToken(token);
    checkIfIsNull(value.getPassword(), Integer.toString(value.getValue()));
    Account account = findByIdOrThrowError(id);
    checkBcryptPassword(value.getPassword(), account.getPasswordAccount());
    valueAboveAcceptable(value.getValue());
    Integer sumValues = account.getAccountBalance() + value.getValue();
    withdrawGreaterThanBalance(sumValues);
    account.setAccountBalance(sumValues);
    accountRepository.save(account);
    return account;
  }

  @Override
  public String bankTransfer(
      String idTransfer,
      String idReceiver,
      String token,
      ValueDto valueDto
  ) {
    negativeValue(valueDto.getValue());
    GlobalMethodsService.verifyToken(token);
    Account accountTransfer = findByIdOrThrowError(idTransfer);
    checkBcryptPassword(valueDto.getPassword(), accountTransfer.getPasswordAccount());
    insufficientBallance(accountTransfer.getAccountBalance(), valueDto.getValue());
    Account accountReceiver = findByIdOrThrowError(idReceiver);
    accountTransfer.setAccountBalance(accountTransfer.getAccountBalance() - valueDto.getValue());
    accountReceiver.setAccountBalance(accountReceiver.getAccountBalance() + valueDto.getValue());
    accountRepository.save(accountTransfer);
    accountRepository.save(accountReceiver);
    return "transfer in the amount of " + valueDto.getValue() + " to id account " + idReceiver;
  }

  @Override
  public void updateAccount(String id, String token, AccountDto accountDto) {
    GlobalMethodsService.verifyToken(token);
    String pwHash = BCrypt.hashpw(accountDto.getPasswordAccount(), BCrypt.gensalt());
    LocalDate localDate = GlobalMethodsService.convertDate(accountDto.getBirthDate());
    Account account = findByIdOrThrowError(id);
    account.buildAccount(accountDto, pwHash, localDate, account.getPerson());
    checkIfIsNotNull(accountDto);
    accountRepository.save(account);
  }

  @Override
  public void delete(String id, String token, PasswordDto password) {
    GlobalMethodsService.verifyToken(token);
    checkIfIsNull(password.getPassword());
    Account account = findByIdOrThrowError(id);
    checkBcryptPassword(password.getPassword(), account.getPasswordAccount());
    accountRepository.deleteById(id);
  }

  /**
   * check if there is a null in accountDto.
   */
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
      String secondPart = ", city, street, district, phoneNumber";
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          firstPart + secondPart + " are required"
      );
    }
  }

  private Account findByIdOrThrowError(String id) {
    Optional<Account> validAccount = accountRepository.findById(id);
    if (validAccount.isEmpty()) {
      throw new AccountNotFoundException();
    }
    return validAccount.get();
  }

  private void checkIfIsNull(String value) {
    if (value == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "all values are required");
    }
  }

  private void checkIfIsNull(String firstValue, String secondValue) {
    if (firstValue == null || secondValue == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "all values are required");
    }
  }

  private void checkBcryptPassword(String passwordDatabase, String password) {
    if (!BCrypt.checkpw(passwordDatabase, password)) {
      throw new IncorrectPasswordException();
    }
  }

  private void valueAboveAcceptable(Integer value) {
    if (value > 2000) {
      throw new ValueDepositException();
    }
  }

  private void withdrawGreaterThanBalance(Integer sumValues) {
    if (sumValues < 0) {
      throw new WithdrawGreaterThanBalanceException();
    }
  }

  private void negativeValue(Integer value) {
    if (value < 0) {
      throw new NegativeValueException();
    }
  }

  private void insufficientBallance(Integer valueTransfer, Integer valueReceiver) {
    if (valueTransfer < valueReceiver) {
      throw new InsufficientBalanceException();
    }
  }
}
