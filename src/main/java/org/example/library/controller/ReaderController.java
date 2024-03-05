package org.example.library.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.example.library.controller.converter.ReaderConverter;
import org.example.library.controller.dto.ErrorDto;
import org.example.library.controller.dto.ReaderDTO;
import org.example.library.entities.Reader;
import org.example.library.service.NotFoundException;
import org.example.library.service.ReaderService;
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
@RequestMapping("/reader")
public class ReaderController {
  private static final Logger LOG = LoggerFactory.getLogger(ReaderController.class);

  @Autowired
  private ReaderService readerService;

  @Autowired
  private ReaderConverter readerConverter;

  @GetMapping
  public ResponseEntity<Object> getReader(@RequestParam("id") long id) {
    try {
      Reader reader = readerService.findById(id);
      if (reader != null) {
        return new ResponseEntity<>(readerConverter.apply(reader), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on reader get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on reader get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping
  public ResponseEntity<Object> createReader(@RequestBody ReaderDTO dto) {
    try {
      Reader entity = readerService.createReader(dto.getFirstName(), dto.getMiddleName(),
          dto.getLastName(), dto.getGender(), dto.getBirthday());
      return new ResponseEntity<>(readerConverter.apply(entity), HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on reader creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on reader creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping
  public ResponseEntity<Object> deleteReader(@RequestParam("id") long id) {
    try {
      readerService.deleteById(id);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on reader delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on reader delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }  }

  @PutMapping
  public ResponseEntity<Object> updateReader(@RequestBody ReaderDTO dto) {
    try {
      Reader updatedReader = readerService.updateReader(dto.getId(), dto.getFirstName(),
          dto.getMiddleName(), dto.getLastName(), dto.getGender(), dto.getBirthday());
      return new ResponseEntity<>(readerConverter.apply(updatedReader), HttpStatus.OK);
    } catch (NotFoundException e) {
      LOG.warn("Not found on reader update, dto=" + dto, e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on reader update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on reader update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  @GetMapping("/findByLastName")
  public ResponseEntity<Object> findByLastName(@RequestParam("lastName") String lastName) {
    try {
      List<ReaderDTO> readerDtos = readerService.findByLastName(lastName).stream()
          .map(readerConverter)
          .collect(Collectors.toList());
      return new ResponseEntity<>(readerDtos, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on reader findByLastName, lastName=" + lastName, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on reader findByLastName, lastName=" + lastName, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
