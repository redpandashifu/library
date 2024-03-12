package org.example.library.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.example.library.controller.converter.AuthorConverter;
import org.example.library.controller.dto.AuthorDTO;
import org.example.library.controller.dto.ErrorDTO;
import org.example.library.entities.Author;
import org.example.library.service.AuthorService;
import org.example.library.service.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/author")
public class AuthorController {
  private static final Logger LOG = LoggerFactory.getLogger(AuthorController.class);

  @Autowired
  private AuthorService authorService;
  @Autowired
  private AuthorConverter authorConverter;

  @PostMapping
  public ResponseEntity<Object> createAuthor(@RequestBody AuthorDTO dto) {
    try {
      Author entity = authorService.createAuthor(dto.getFirstName(), dto.getLastName());
      return new ResponseEntity<>(authorConverter.apply(entity), HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on author creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on author creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping
  public ResponseEntity<Object> updateAuthor(@RequestBody AuthorDTO dto) {
    try {
      Author updatedAuthor =
          authorService.updateAuthor(dto.getId(), dto.getFirstName(), dto.getLastName());
      return new ResponseEntity<>(authorConverter.apply(updatedAuthor), HttpStatus.OK);
    } catch (NotFoundException e) {
      LOG.warn("Not found on author update, dto=" + dto, e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on author update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on author update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping
  public ResponseEntity<Object> getAuthor(@RequestParam("id") long id) {
    try {
      Author author = authorService.findById(id);
      if (author != null) {
        return new ResponseEntity<>(authorConverter.apply(author), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on author get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on author get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping
  public ResponseEntity<Object> deleteAuthor(@RequestParam("id") long id) {
    try {
      authorService.deleteById(id);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on author delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on author delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/findByLastName")
  public ResponseEntity<Object> findByLastName(@RequestParam("lastName") String lastName) {
    try {
      List<AuthorDTO> authorDtos = authorService.findByLastName(lastName).stream()
          .map(authorConverter)
          .collect(Collectors.toList());
      return new ResponseEntity<>(authorDtos, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on author findByLastName, lastName=" + lastName, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on author findByLastName, lastName=" + lastName, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
