package com.java.spring.controller;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ControllerInterface.
 */
@RequestMapping("/person")
public interface PersonControllerInterface {

  @PostMapping
  public ResponseEntity<Person> create(@RequestBody PersonDto personDto);

  @GetMapping
  public ResponseEntity<List<Person>> findAll(
      @RequestHeader(value = "token", defaultValue = "") String token
  );

  @PostMapping("/login")
  public ResponseEntity<String> generateToken(@RequestBody PersonDto personDto);

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteById(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable Long id
  );

}
