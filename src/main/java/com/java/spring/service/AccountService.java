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
      String token, String id, ValueDto value) {
    try {
      GlobalMethodsService.verifyToken(token);
      Account account = findByIdOrThrowError(id);
      if (!BCrypt.checkpw(value.getPassword(), account.getPasswordAccount())) {
        throw new IncorrectPasswordException();
      }
      if (value.getValue() > 2000) {
        throw new ValueDepositException();
      }
      Integer sumValues = account.getAccountBalance() + value.getValue();
      if (sumValues < 0) {
        throw new WithdrawGreaterThanBalanceException();
      }
      account.setAccountBalance(sumValues);
      accountRepository.save(account);
      return account;
    } catch (NullPointerException e) {
      throw new NullPointerException("all values are required");
    }
  }

  @Override
  public String bankTransfer(
      String idTransfer, String idReceiver, String token, ValueDto valueDto) {
    if (valueDto.getValue() < 0) {
      throw new NegativeValueException();
    }
    GlobalMethodsService.verifyToken(token);
    Account accountTransfer = findByIdOrThrowError(idTransfer);
    checkBcryptPassword(valueDto.getPassword(), accountTransfer.getPasswordAccount());
    if (accountTransfer.getAccountBalance() < valueDto.getValue()) {
      throw new InsufficientBalanceException();
    }
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
    Account account = findByIdOrThrowError(id);
    account.setEmail(accountDto.getEmail());
    String pwHash = BCrypt.hashpw(accountDto.getPasswordAccount(), BCrypt.gensalt());
    account.setPasswordAccount(pwHash);
    LocalDate localDate = GlobalMethodsService.convertDate(accountDto.getBirthDate());
    account.setBirthDate(localDate);
    account.setCountry(accountDto.getCountry());
    account.setState(accountDto.getState());
    account.setCity(accountDto.getCity());
    account.setDistrict(accountDto.getDistrict());
    account.setStreet(accountDto.getStreet());
    account.setPhoneNumber(accountDto.getPhoneNumber());
    checkIfIsNotNull(accountDto);
    accountRepository.save(account);
  }

  @Override
  public void delete(String id, String token, PasswordDto password) {
    try {
      GlobalMethodsService.verifyToken(token);
      Account account = findByIdOrThrowError(id);
      checkBcryptPassword(password.getPassword(), account.getPasswordAccount());
      accountRepository.deleteById(id);
    } catch (NullPointerException e) {
      throw new NullPointerException("'password' is required");
    }

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
      throw new NullPointerException(firstPart + secondPart + " are required");
    }
  }

  private Account findByIdOrThrowError(String id) {
    Optional<Account> validAccount = accountRepository.findById(id);
    if (validAccount.isEmpty()) {
      throw new AccountNotFoundException();
    }
    return validAccount.get();
  }

  private void checkBcryptPassword(String passwordDatabase, String password) {
    if (!BCrypt.checkpw(passwordDatabase, password)) {
      throw new IncorrectPasswordException();
    }
  }
}
