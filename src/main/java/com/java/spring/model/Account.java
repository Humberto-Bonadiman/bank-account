package com.java.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Objects;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordAccount() {
    return passwordAccount;
  }

  public void setPasswordAccount(String passwordAccount) {
    this.passwordAccount = passwordAccount;
  }

  public Integer getAccountBalance() {
    return accountBalance;
  }

  public void setAccountBalance(Integer accountBalance) {
    this.accountBalance = accountBalance;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        birthDate,
        city,
        country,
        district,
        email,
        id,
        passwordAccount,
        accountBalance,
        person,
        phoneNumber,
        state,
        street
    );
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Account other = (Account) obj;
    return Objects.equals(birthDate, other.birthDate)
        && Objects.equals(accountBalance, other.accountBalance)
        && Objects.equals(city, other.city)
        && Objects.equals(country, other.country)
        && Objects.equals(district, other.district)
        && Objects.equals(email, other.email)
        && Objects.equals(id, other.id)
        && Objects.equals(passwordAccount, other.passwordAccount)
        && Objects.equals(person, other.person)
        && Objects.equals(phoneNumber, other.phoneNumber)
        && Objects.equals(state, other.state)
        && Objects.equals(street, other.street);
  }
}
