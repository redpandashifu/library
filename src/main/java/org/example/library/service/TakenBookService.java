package org.example.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.library.entities.Book;
import org.example.library.entities.Reader;
import org.example.library.entities.TakenBook;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.ReaderRepository;
import org.example.library.repositories.TakenBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TakenBookService {
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private ReaderRepository readerRepository;
  @Autowired
  private TakenBookRepository takenBookRepository;

  public TakenBookService(BookRepository bookRepository, ReaderRepository readerRepository,
      TakenBookRepository takenBookRepository) {
    this.bookRepository = bookRepository;
    this.readerRepository = readerRepository;
    this.takenBookRepository = takenBookRepository;
  }

  @Transactional
  public TakenBook createTakenBook(Long readerId, Long bookId, LocalDate dateFrom,
      LocalDate dateTo) {
    try {
      validate(dateFrom, dateTo);
      Reader reader = validateAndGetReader(readerId);
      Book book = validateAndGetBook(bookId);
      return takenBookRepository.saveAndFlush(new TakenBook(reader, book, dateFrom, dateTo));
    } catch (DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public TakenBook updateTakenBook(Long id, Long readerId, Long bookId, LocalDate dateFrom,
      LocalDate dateTo) {
    try {
      if (id == null) {
        throw new IllegalArgumentException("Taken book id is not specified");
      }

      validate(dateFrom, dateTo);
      Reader reader = validateAndGetReader(readerId);
      Book book = validateAndGetBook(bookId);

      Optional<TakenBook> optionalTakenBook = takenBookRepository.findById(id);
      if (!optionalTakenBook.isPresent()) {
        throw new NotFoundException("Taken book with id=" + id + " doesn't exist");
      }

      TakenBook takenBook = optionalTakenBook.get();
      takenBook.setReader(reader);
      takenBook.setBook(book);
      takenBook.setDateFrom(dateFrom);
      takenBook.setDateTo(dateTo);
      return takenBookRepository.saveAndFlush(takenBook);
    } catch(DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public TakenBook findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Taken book id is not specified");
    }
    Optional<TakenBook> optionalTakenBook = takenBookRepository.findById(id);
    return optionalTakenBook.orElse(null);
  }

  @Transactional
  public void deleteById(Long id) {
    try {
      if (id == null) {
        throw new IllegalArgumentException("Taken book id is not specified");
      }
      takenBookRepository.deleteById(id);
    } catch (EmptyResultDataAccessException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public List<TakenBook> findByReaderAndPeriod(Long readerId, LocalDate dateFrom, LocalDate dateTo) {
    if (dateFrom == null) {
      throw new IllegalArgumentException("Period date from is not specified");
    }
    if (dateTo == null) {
      throw new IllegalArgumentException("Period date to is not specified");
    }
    if (!dateTo.isAfter(dateFrom)) {
      throw new IllegalArgumentException("Period date to is not after date from");
    }
    if (readerId == null) {
      throw new IllegalArgumentException("Reader id is not specified");
    }
    return takenBookRepository.findByReaderAndPeriod(readerId, dateFrom, dateTo);
  }

  private void validate(LocalDate dateFrom, LocalDate dateTo) {
    if (dateFrom == null) {
      throw new IllegalArgumentException("Date when book was taken is not specified");
    }
    if (dateTo == null) {
      throw new IllegalArgumentException("Date when book should be returned is not specified");
    }
    if (!dateTo.isAfter(dateFrom)) {
      throw new IllegalArgumentException("Date when book is returned is not after taken date");
    }
  }

  private Reader validateAndGetReader(Long readerId) {
    if (readerId == null) {
      throw new IllegalArgumentException("Reader id is not specified");
    }

    Optional<Reader> optionalReader = readerRepository.findById(readerId);
    if(!optionalReader.isPresent()) {
      throw new IllegalArgumentException("Reader id is not found");
    }
    return optionalReader.get();
  }

  private Book validateAndGetBook(Long bookId) {
    if (bookId == null) {
      throw new IllegalArgumentException("Book id is not specified");
    }

    Optional<Book> optionalBook = bookRepository.findById(bookId);
    if(!optionalBook.isPresent()) {
      throw new IllegalArgumentException("Book id is not found");
    }
    return optionalBook.get();
  }
}
