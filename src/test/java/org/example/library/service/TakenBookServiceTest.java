package org.example.library.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.example.library.entities.Author;
import org.example.library.entities.Book;
import org.example.library.entities.Gender;
import org.example.library.entities.Reader;
import org.example.library.entities.TakenBook;
import org.example.library.repositories.AuthorRepository;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.ReaderRepository;
import org.example.library.repositories.TakenBookRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;


public class TakenBookServiceTest {

  private TakenBookRepository takenBookRepository;
  private TakenBookService takenBookService;
  private ReaderRepository readerRepository;
  private AuthorRepository authorRepository;
  private BookRepository bookRepository;
  private Book book;
  private Reader reader;
  private List<Author> authors;
  private final Long takenBookId = 399L;
  private TakenBook takenBook;

  class Parameters {

    Long readerId;
    Long bookId;
    LocalDate dateFrom;
    LocalDate dateTo;

    public Parameters(Long readerId, Long bookId, LocalDate dateFrom, LocalDate dateTo) {
      this.readerId = readerId;
      this.bookId = bookId;
      this.dateFrom = dateFrom;
      this.dateTo = dateTo;
    }
  }

  @Before
  public void setUp() {
    takenBookRepository = Mockito.mock(TakenBookRepository.class);
    readerRepository = Mockito.mock(ReaderRepository.class);
    authorRepository = Mockito.mock(AuthorRepository.class);
    bookRepository = Mockito.mock(BookRepository.class);
    takenBookService = new TakenBookService(bookRepository, readerRepository, takenBookRepository);

    Author author = new Author("Craig", "Walls");
    author.setId(99L);
    authors = Arrays.asList(author);
    when(authorRepository.findByIds(any())).thenReturn(authors);

    book = new Book("Spring in action", authors, 2022);
    book.setId(199L);
    when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

    reader = new Reader("Max", "Sergeevich", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12));
    reader.setId(299L);
    when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));

    takenBook = new TakenBook(reader, book, LocalDate.of(2024, 3, 20),
        LocalDate.of(2024, 3, 25));
    takenBook.setId(takenBookId);
  }

  @Test
  public void testCreateTakenBookSuccess() {
    when(takenBookRepository.saveAndFlush(any())).thenReturn(takenBook);

    TakenBook result = takenBookService.createTakenBook(reader.getId(), book.getId(),
        takenBook.getDateFrom(), takenBook.getDateTo());

    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getReader());
    Assert.assertNotNull(result.getBook());
    Assert.assertNotNull(result.getDateFrom());
    Assert.assertNotNull(result.getDateTo());
    Assert.assertEquals(takenBook.getId(), result.getId());
    Assert.assertEquals(takenBook.getReader().getId(), result.getReader().getId());
    Assert.assertEquals(takenBook.getBook().getId(), result.getBook().getId());
    Assert.assertTrue(takenBook.getDateFrom().equals(result.getDateFrom()));
    Assert.assertTrue(takenBook.getDateTo().equals(result.getDateTo()));
  }

  @Test
  public void testCreateTakenBookValidationException() {
    List<Parameters> parameters = Arrays.asList(
        new Parameters(null, book.getId(), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(2990L, book.getId(), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), null, LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), 1990L, LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), book.getId(), null, LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), book.getId(), LocalDate.of(2024, 3, 20), null),
        new Parameters(reader.getId(), book.getId(), LocalDate.of(2024, 3, 20),
            LocalDate.of(2024, 3, 20)));

    for (Parameters parameter : parameters) {
      try {
        takenBookService.createTakenBook(parameter.readerId, parameter.bookId, parameter.dateFrom,
            parameter.dateTo);
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testCreateTakenBookIntegrityException() {
    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(takenBookRepository).saveAndFlush(any());

    try {
      takenBookService.createTakenBook(reader.getId(), book.getId(),
          LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25));
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testUpdateTakenBookSuccess() {
    when(takenBookRepository.findById(takenBook.getId())).thenReturn(Optional.of(takenBook));
    when(takenBookRepository.saveAndFlush(any())).thenReturn(takenBook);

    TakenBook result = takenBookService.updateTakenBook(takenBook.getId(),
        takenBook.getReader().getId(),
        takenBook.getBook().getId(),
        takenBook.getDateFrom(),
        takenBook.getDateTo());

    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getReader());
    Assert.assertNotNull(result.getBook());
    Assert.assertNotNull(result.getDateFrom());
    Assert.assertNotNull(result.getDateTo());
    Assert.assertEquals(takenBook.getId(), result.getId());
    Assert.assertEquals(takenBook.getReader().getId(), result.getReader().getId());
    Assert.assertEquals(takenBook.getBook().getId(), result.getBook().getId());
    Assert.assertEquals(takenBook.getDateFrom(), result.getDateFrom());
    Assert.assertEquals(takenBook.getDateTo(), result.getDateTo());
  }

  @Test
  public void testUpdateTakenBookValidationException() {
    List<Parameters> parameters = Arrays.asList(
        new Parameters(null, book.getId(), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(2990L, book.getId(), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), null, LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), 1990L, LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), book.getId(), null, LocalDate.of(2024, 3, 25)),
        new Parameters(reader.getId(), book.getId(), LocalDate.of(2024, 3, 20), null),
        new Parameters(reader.getId(), book.getId(), LocalDate.of(2024, 3, 20),
            LocalDate.of(2024, 3, 20)));

    for (Parameters parameter : parameters) {
      try {
        takenBookService.updateTakenBook(takenBookId, parameter.readerId, parameter.bookId,
            parameter.dateFrom,
            parameter.dateTo);
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testUpdateTakenBookIntegrityException() {
    when(takenBookRepository.findById(takenBook.getId())).thenReturn(Optional.of(takenBook));

    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(takenBookRepository).saveAndFlush(any());

    try {
      takenBookService.updateTakenBook(takenBookId, reader.getId(), book.getId(),
          LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 30));

    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testFindByIdSuccess() {
    when(takenBookRepository.findById(takenBook.getId())).thenReturn(Optional.of(takenBook));

    TakenBook result = takenBookService.findById(takenBook.getId());

    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getReader());
    Assert.assertNotNull(result.getBook());
    Assert.assertNotNull(result.getDateFrom());
    Assert.assertNotNull(result.getDateTo());
    Assert.assertEquals(takenBook.getId(), result.getId());
    Assert.assertEquals(takenBook.getReader().getId(), result.getReader().getId());
    Assert.assertEquals(takenBook.getBook().getId(), result.getBook().getId());
    Assert.assertTrue(takenBook.getDateFrom().equals(result.getDateFrom()));
    Assert.assertTrue(takenBook.getDateTo().equals(result.getDateTo()));
  }

  @Test
  public void testFindByIdSuccessNotFound() {
    when(takenBookRepository.findById(any())).thenReturn(Optional.empty());

    TakenBook result = takenBookService.findById(1L);
    Assert.assertNull(result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByIdValidationException() {
    takenBookService.findById(null);
  }

  @Test
  public void testDeleteByIdSuccess() {
    takenBookService.deleteById(1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdNotFound() {
    EmptyResultDataAccessException exception = new EmptyResultDataAccessException(0);
    doThrow(exception).when(takenBookRepository).deleteById(any());

    takenBookService.deleteById(1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdValidationException() {
    takenBookService.deleteById(null);
  }

  @Test
  public void testFindByReaderAndPeriodSuccess() {
    long readerId = 599L;
    LocalDate dateFrom = LocalDate.of(2024, 3, 5);
    LocalDate dateTo = LocalDate.of(2024, 4, 6);
    when(takenBookRepository.findByReaderAndPeriod(readerId, dateFrom, dateTo))
        .thenReturn(Collections.singletonList(takenBook));

    List<TakenBook> takenBooks = takenBookService.findByReaderAndPeriod(readerId, dateFrom, dateTo);

    Assert.assertNotNull(takenBooks);
    Assert.assertEquals(1, takenBooks.size());
    TakenBook actual = takenBooks.get(0);
    Assert.assertNotNull(actual);
    Assert.assertEquals(actual.getId(), takenBook.getId());
    Assert.assertSame(actual.getBook(), takenBook.getBook());
    Assert.assertSame(actual.getReader(), takenBook.getReader());
    Assert.assertSame(actual.getDateFrom(), takenBook.getDateFrom());
    Assert.assertSame(actual.getDateTo(), takenBook.getDateTo());
  }

  @Test
  public void testFindByReaderAndPeriodSuccessNotFound() {
    long readerId = 599L;
    LocalDate dateFrom = LocalDate.of(2024, 3, 5);
    LocalDate dateTo = LocalDate.of(2024, 4, 6);
    when(takenBookRepository.findByReaderAndPeriod(readerId, dateFrom, dateTo))
        .thenReturn(Collections.emptyList());

    List<TakenBook> result = takenBookService.findByReaderAndPeriod(readerId, dateFrom, dateTo);
    Assert.assertNotNull(result);
    Assert.assertEquals(0, result.size());
  }

  @Test
  public void testFindByReaderAndPeriodValidationException() {
    List<ReaderAndPeriod> readerAndPeriods = Arrays.asList(
        new ReaderAndPeriod(null, LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 25)),
        new ReaderAndPeriod(reader.getId(), null, LocalDate.of(2024, 3, 25)),
        new ReaderAndPeriod(reader.getId(), LocalDate.of(2024, 3, 20), null),
        new ReaderAndPeriod(reader.getId(), LocalDate.of(2024, 3, 25), LocalDate.of(2024, 3, 20)),
        new ReaderAndPeriod(reader.getId(), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 20))
    );

    for (ReaderAndPeriod readerAndPeriod : readerAndPeriods) {
      try {
        takenBookService.findByReaderAndPeriod(readerAndPeriod.readerId, readerAndPeriod.dateFrom,
            readerAndPeriod.dateTo);
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  private static class ReaderAndPeriod {

    private Long readerId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public ReaderAndPeriod(Long reader, LocalDate dateFrom, LocalDate dateTo) {
      this.readerId = reader;
      this.dateFrom = dateFrom;
      this.dateTo = dateTo;
    }
  }
}