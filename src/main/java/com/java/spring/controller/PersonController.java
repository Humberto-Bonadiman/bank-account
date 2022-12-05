package com.java.spring.controller;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import com.java.spring.service.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Person endpoint")
@CrossOrigin
@RestController
public class PersonController implements PersonControllerInterface {

  @Autowired
  PersonService personService;

  @Operation(summary = "Create person data")
  public ResponseEntity<Person> create(PersonDto personDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(personService.create(personDto));
  }

  @Operation(summary = "Generate token by person data")
  public ResponseEntity<String> generateToken(PersonDto personDto) {
    return ResponseEntity.status(HttpStatus.OK).body(personService.generateToken(personDto));
  }

  @Operation(summary = "List all registered people")
  public ResponseEntity<List<Person>> findAll(String token) {
    return ResponseEntity.status(HttpStatus.OK).body(personService.findAll(token));
  }

  @Operation(summary = "Delete person data by id")
  public ResponseEntity<Object> deleteById(String token, Long id) {
    personService.deleteById(token, id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
