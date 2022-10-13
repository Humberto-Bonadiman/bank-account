package com.java.spring.repository;

import com.java.spring.model.Person;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

  Person findFirstByCpf(String cpf);

  public Optional<Person> findByCpf(String cpf);

}
