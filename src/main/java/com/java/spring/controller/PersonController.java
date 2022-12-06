package com.java.spring.controller;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;
import com.java.spring.service.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Create a person", 
          content = { @Content(mediaType = "application/json", 
          schema = @Schema(implementation = Person.class)) }),
      @ApiResponse(responseCode = "400", description = "Wrong format",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Person already registered",
          content = @Content)})
  public ResponseEntity<Person> create(PersonDto personDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(personService.create(personDto));
  }

  @Operation(summary = "Generate token by person data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Generate a token", 
          content = { @Content(mediaType = "application/json", 
          schema = @Schema(implementation = String.class)) }),
      @ApiResponse(responseCode = "400", description = "Wrong format",
          content = @Content)})
  public ResponseEntity<String> generateToken(PersonDto personDto) {
    return ResponseEntity.status(HttpStatus.OK).body(personService.generateToken(personDto));
  }

  @Operation(summary = "List all registered people")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Generate a token", 
          content = { @Content(mediaType = "application/json", 
          array = @ArraySchema(schema = @Schema(implementation = Person.class))) }),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content)})
  public ResponseEntity<List<Person>> findAll(String token) {
    return ResponseEntity.status(HttpStatus.OK).body(personService.findAll(token));
  }

  @Operation(summary = "Delete person data by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "400", description = "Wrong format",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Person not found",
          content = @Content)})
  public ResponseEntity<Object> deleteById(String token, Long id) {
    personService.deleteById(token, id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
