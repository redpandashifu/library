package org.example.library.controller;

import org.example.library.controller.converter.BookConverter;
import org.example.library.controller.dto.BookDTO;
import org.example.library.controller.dto.ErrorDto;
import org.example.library.entities.Book;
import org.example.library.service.BookService;
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
@RequestMapping("/book")
public class BookController {
  private static final Logger LOG = LoggerFactory.getLogger(BookController.class);

  @Autowired
  private BookConverter bookConverter;

  @Autowired
  private BookService bookService;

  @PostMapping
  public ResponseEntity<Object> createBook(@RequestBody BookDTO dto) {
    try {
      Book book = bookService.createBook(dto.getTitle(), dto.getPublished(), dto.getAuthorIds());
      return new ResponseEntity<>(bookConverter.apply(book), HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on book creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on book creation, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping
  public ResponseEntity<Object> updateBook(@RequestBody BookDTO dto) {
    try {
      Book updatedBook = bookService.updateBook(dto.getId(), dto.getTitle(), dto.getPublished(),
          dto.getAuthorIds());
      return new ResponseEntity<>(bookConverter.apply(updatedBook), HttpStatus.OK);
    } catch (NotFoundException e) {
      LOG.warn("Not found on book update, dto=" + dto, e);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on book update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on book update, dto=" + dto, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping
  public ResponseEntity<Object> getBook(@RequestParam("id") long id) {
    try {
      Book book = bookService.findById(id);
      if (book != null) {
        return new ResponseEntity<>(bookConverter.apply(book), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on book get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on book get, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping
  public ResponseEntity<Object> deleteBook(@RequestParam("id") long id) {
    try {
      bookService.deleteById(id);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.warn("Bad request on book delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.warn("Internal server error on book delete, id=" + id, e);
      return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
