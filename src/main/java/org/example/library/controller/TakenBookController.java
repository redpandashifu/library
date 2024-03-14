package org.example.library.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.example.library.controller.converter.TakenBookConverter;
import org.example.library.controller.dto.ErrorDTO;
import org.example.library.controller.dto.TakenBookDTO;
import org.example.library.entities.TakenBook;
import org.example.library.service.NotFoundException;
import org.example.library.service.TakenBookService;
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
@RequestMapping("/takenBook")
public class TakenBookController {
  private static final Logger LOG = LoggerFactory.getLogger(TakenBookController.class);
  
  @Autowired
  private TakenBookService takenBookService;

  @Autowired
  private TakenBookConverter takenBookConverter;

  @PostMapping
  public ResponseEntity<Object> createTakenBook(@RequestBody TakenBookDTO dto) {
    try {
      TakenBook entity = takenBookService.createTakenBook(dto.getReaderId(), dto.getBookId(),
          dto.getDateFrom(), dto.getDateTo());
      return new ResponseEntity<>(takenBookConverter.apply(entity), HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on taken book creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on taken book creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping
  public ResponseEntity<Object> updateTakenBook(@RequestBody TakenBookDTO dto) {
    try {
      TakenBook updatedTakenBook = takenBookService.updateTakenBook(dto.getId(), dto.getReaderId(), 
          dto.getBookId(), dto.getDateFrom(), dto.getDateTo());
      return new ResponseEntity<>(takenBookConverter.apply(updatedTakenBook), HttpStatus.OK);
    } catch (NotFoundException e) {
      LOG.warn("Not found on taken book update, dto=" + dto, e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on taken book update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on taken book update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping
  public ResponseEntity<Object> getAuthor(@RequestParam("id") long id) {
    try {
      TakenBook takenBook = takenBookService.findById(id);
      if (takenBook != null) {
        return new ResponseEntity<>(takenBookConverter.apply(takenBook), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on taken book get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on taken book get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping
  public ResponseEntity<Object> deleteAuthor(@RequestParam("id") long id) {
    try {
      takenBookService.deleteById(id);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on taken book delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on taken book delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/findByPeriod")
  public ResponseEntity<Object> getTakenBooksByPeriod(@RequestParam("readerId") long readerId,
      @RequestParam("from") LocalDate dateFrom, @RequestParam("to") LocalDate dateTo) {
    try {
      List<TakenBook> takenBooks = takenBookService.findByReaderAndPeriod(readerId, dateFrom, dateTo);
      return new ResponseEntity<>(
          takenBooks.stream().map(takenBookConverter).collect(Collectors.toList()), HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on find by period, readerId=" + readerId + ", from=" + dateFrom
          + ", to=" + dateTo, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Bad request on find by period, readerId=" + readerId + ", from=" + dateFrom
          + ", to=" + dateTo, e);
      return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
