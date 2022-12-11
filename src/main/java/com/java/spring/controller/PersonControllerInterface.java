package com.java.spring.controller;

import com.java.spring.dto.PersonDto;
import com.java.spring.model.Person;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Create a person", 
          content = { @Content(mediaType = "application/json", 
          schema = @Schema(implementation = Person.class)) }),
      @ApiResponse(responseCode = "400", description = "Wrong format",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Person already registered",
          content = @Content)})
  public ResponseEntity<Person> create(@RequestBody PersonDto personDto);

  @GetMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Find all people", 
          content = { @Content(mediaType = "application/json", 
          array = @ArraySchema(schema = @Schema(implementation = Person.class))) }),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content)})
  public ResponseEntity<List<Person>> findAll(
      @RequestHeader(value = "token", defaultValue = "") String token
  );

  @PostMapping("/login")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Generate a token", 
          content = { @Content(mediaType = "application/json", 
          schema = @Schema(implementation = String.class)) }),
      @ApiResponse(responseCode = "400", description = "Wrong format",
          content = @Content)})
  public ResponseEntity<String> generateToken(@RequestBody PersonDto personDto);

  @DeleteMapping("/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unathorized",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Person not found",
          content = @Content)})
  public ResponseEntity<Object> deleteById(
      @RequestHeader(value = "token", defaultValue = "") String token,
      @PathVariable Long id
  );

}
