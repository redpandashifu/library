package org.example.library.service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.library.entities.Author;
import org.example.library.entities.Book;
import org.example.library.repositories.AuthorRepository;
import org.example.library.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
  }

  @Transactional
  public Book createBook(String title, Set<Long> authorIds, Integer published) {
    try {
      validate(title, published);
      List<Author> authors = validateAndGetAuthors(authorIds);
      return bookRepository.saveAndFlush(new Book(title, authors, published));
    } catch(DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public Book updateBook(Long id, String title, Set<Long> authorIds, Integer published) {
    try {
      if (id == null) {
        throw new IllegalArgumentException("Book id is not specified");
      }
      validate(title, published);

      Optional<Book> optionalBook = bookRepository.findById(id);
      if (!optionalBook.isPresent()) {
        throw new NotFoundException("Book with id=" + id + " doesn't exist");
      }

      List<Author> authors = validateAndGetAuthors(authorIds);
      Book book = optionalBook.get();
      book.setTitle(title);
      book.setPublished(published);
      book.setAuthors(authors);
      return bookRepository.saveAndFlush(book);
    } catch(DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public Book findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Book id is not specified");
    }
    Optional<Book> optionalBook = bookRepository.findById(id);
    return optionalBook.orElse(null);
  }

  @Transactional
  public void deleteById(Long id) {
    try {
      if (id == null) {
        throw new IllegalArgumentException("Book id is not specified");
      }
      bookRepository.deleteById(id);
    } catch (EmptyResultDataAccessException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public List<Book> findByTitle(String title) {
    if (title == null || title.isEmpty()) {
      throw new IllegalArgumentException("Title is not specified");
    }
    return bookRepository.findByTitle(title);
  }

  private void validate(String title, Integer published) {
    if (title == null || title.isEmpty()) {
      throw new IllegalArgumentException("Book title is null or empty");
    }
    if (published == null) {
      throw new IllegalArgumentException("Book published year is null");
    }
    if (published < 0) {
      throw new IllegalArgumentException("Book published year is below zero");
    }
    if (published > Year.now().getValue()) {
      throw new IllegalArgumentException("Book published year is greater than current");
    }
  }

  private List<Author> validateAndGetAuthors(Set<Long> authorIds) {
    if (authorIds == null) {
      throw new IllegalArgumentException("Book author ids is null");
    }

    authorIds = authorIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

    if (authorIds.isEmpty()) {
      throw new IllegalArgumentException("Book author ids is empty or contain only null values");
    }

    Map<Long, Author> authorById = authorRepository.findByIds(authorIds).stream()
        .collect(Collectors.toMap(Author::getId, (r) -> r));
    Set<Long> notFoundIds =
        authorIds.stream().filter(id -> !authorById.containsKey(id)).collect(Collectors.toSet());
    if (!notFoundIds.isEmpty()) {
      throw new IllegalArgumentException("Book author ids are not found: " + notFoundIds);
    }
    return new ArrayList<>(authorById.values());
  }
}
