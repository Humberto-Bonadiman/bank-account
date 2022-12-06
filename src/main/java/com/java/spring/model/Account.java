package com.java.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.spring.dto.AccountDto;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "account")
public class Account {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String passwordAccount;

  @Column(nullable = false)
  private Integer accountBalance;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Column(nullable = false)
  private String country;

  @Column(nullable = false)
  private String state;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String street;

  @Column(nullable = false)
  private String district;

  @Column(nullable = false)
  private String phoneNumber;

  @JsonIgnore
  @JoinColumn(name = "person_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY)
  private Person person;

  public void setId(String id) {
    this.id = id;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPasswordAccount(String passwordAccount) {
    this.passwordAccount = passwordAccount;
  }

  public void setAccountBalance(Integer accountBalance) {
    this.accountBalance = accountBalance;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordAccount() {
    return passwordAccount;
  }

  public Integer getAccountBalance() {
    return accountBalance;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public String getCountry() {
    return country;
  }

  public String getState() {
    return state;
  }

  public String getCity() {
    return city;
  }

  public String getStreet() {
    return street;
  }

  public String getDistrict() {
    return district;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public Person getPerson() {
    return person;
  }

  /**
   * build account.
   */
  public void buildAccount(
      AccountDto accountDto,
      String pwHash,
      LocalDate localDate,
      Person person
  ) {
    setEmail(accountDto.getEmail());
    setPasswordAccount(pwHash);
    setAccountBalance(0);
    setBirthDate(localDate);
    setCountry(accountDto.getCountry());
    setState(accountDto.getState());
    setCity(accountDto.getCity());
    setStreet(accountDto.getStreet());
    setDistrict(accountDto.getDistrict());
    setPhoneNumber(accountDto.getPhoneNumber());
    setPerson(person);
  }
}
