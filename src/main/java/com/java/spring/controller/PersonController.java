package com.java.spring.controller;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import com.java.spring.service.PersonService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/person")
public class PersonController implements PersonControllerInterface<PersonDto, Person> {

  @Autowired
  PersonService personService;

  @PostMapping
  public ResponseEntity<Person> create(@RequestBody PersonDto personDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(personService.create(personDto));
  }

  @PostMapping("/login")
  public ResponseEntity<String> generateToken(@RequestBody PersonDto personDto) {
    return ResponseEntity.status(HttpStatus.OK).body(personService.generateToken(personDto));
  }

  @GetMapping
  public ResponseEntity<List<Person>> findAll(
      @RequestHeader(value="token", defaultValue = "") String token
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(personService.findAll(token));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteById(
    @RequestHeader(value="token", defaultValue = "") String token,
    @PathVariable Long id
  ) {
    personService.deleteById(token, id);
	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
