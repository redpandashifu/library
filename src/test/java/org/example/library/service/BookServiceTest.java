package org.example.library.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.library.entities.Author;
import org.example.library.entities.Book;
import org.example.library.repositories.AuthorRepository;
import org.example.library.repositories.BookRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

public class BookServiceTest {

  private BookService bookService;
  private BookRepository bookRepository;
  private AuthorRepository authorRepository;

  private Author author;
  private List<Author> authors;
  private Book book;

  class Parameter {

    String title;
    Set<Long> authorIds;
    Integer published;

    public Parameter(String title, Set<Long> authorIds, Integer published) {
      this.title = title;
      this.authorIds = authorIds;
      this.published = published;
    }
  }

  @Before
  public void setUp() {
    bookRepository = Mockito.mock(BookRepository.class);
    authorRepository = Mockito.mock(AuthorRepository.class);
    bookService = new BookService(bookRepository, authorRepository);

    author = new Author("Craig", "Walls");
    author.setId(99L);
    authors = Arrays.asList(author);
    when(authorRepository.findByIds(any())).thenReturn(authors);

    book = new Book("Spring in Action", authors, 2022);
    book.setId(199L);
    when(bookRepository.findById(any())).thenReturn(Optional.of(book));
  }

  @Test
  public void testCreateBookSuccess() {
    when(bookRepository.saveAndFlush(any())).thenReturn(book);

    Book result = bookService.createBook(book.getTitle(), Collections.singleton(author.getId()),
        book.getPublished());

    Assert.assertNotNull(result);
    Assert.assertEquals(book.getTitle(), result.getTitle());
    Assert.assertEquals(book.getPublished(), result.getPublished());
    Assert.assertNotNull(result.getAuthors());
    Assert.assertEquals(1, result.getAuthors().size());
    Assert.assertEquals(book.getAuthors().get(0), result.getAuthors().get(0));
  }

  @Test
  public void testCreateBookValidationException() {
    Set<Long> authorIds = authors.stream().map(Author::getId).collect(Collectors.toSet());
    List<Parameter> parameters = Arrays.asList(
        new Parameter("", authorIds, 2022),
        new Parameter(null, authorIds, 2022),
        new Parameter("Spring in Action", Collections.EMPTY_SET, 2022),
        new Parameter("Spring in Action", null, 2022),
        new Parameter("Spring in Action", authorIds, null),
        new Parameter("Spring in Action", authorIds, Year.now().getValue() + 1)
    );

    for (Parameter parameter : parameters) {
      try {
        bookService.createBook(parameter.title, parameter.authorIds, parameter.published);
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testCreateBookIntegrityException() {
    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(bookRepository).saveAndFlush(any());

    try {
      bookService.createBook("Spring in Action",
          authors.stream().map(Author::getId).collect(Collectors.toSet()),
          2022);
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testUpdateBookSuccess() {
    when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
    when(bookRepository.saveAndFlush(any())).thenReturn(book);

    Book result = bookService.updateBook(book.getId(), book.getTitle(),
        book.getAuthors().stream().map(Author::getId).collect(Collectors.toSet()),
        book.getPublished());

    Assert.assertNotNull(result);
    Assert.assertEquals(book.getId(), result.getId());
    Assert.assertEquals(book.getTitle(), result.getTitle());
    Assert.assertEquals(book.getAuthors(), result.getAuthors());
    Assert.assertEquals(book.getPublished(), result.getPublished());
  }

  @Test
  public void testUpdateBookValidationException() {
    Set<Long> authorIds = authors.stream().map(Author::getId).collect(Collectors.toSet());
    List<Parameter> parameters = Arrays.asList(
        new Parameter("", authorIds, 2022),
        new Parameter(null, authorIds, 2022),
        new Parameter("Spring in Action", Collections.EMPTY_SET, 2022),
        new Parameter("Spring in Action", null, 2022),
        new Parameter("Spring in Action", authorIds, null),
        new Parameter("Spring in Action", authorIds, Year.now().getValue() + 1)
    );

    for (Parameter parameter : parameters) {
      try {
        bookService.updateBook(book.getId(), parameter.title, parameter.authorIds,
            parameter.published);
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testUpdateAuthorIntegrityException() {
    when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(bookRepository).saveAndFlush(any());

    Set<Long> authorIds = book.getAuthors().stream().map(Author::getId).collect(Collectors.toSet());
    try {
      bookService.updateBook(199L, "Spring", authorIds, 2020);
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testFindByIdSuccess() {
    when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

    Book result = bookService.findById(book.getId());

    Assert.assertNotNull(result);
    Assert.assertEquals(book.getId(), result.getId());
    Assert.assertEquals(book.getTitle(), result.getTitle());

    Assert.assertTrue(book.getAuthors().equals(result.getAuthors()));
    Assert.assertEquals(book.getPublished(), result.getPublished());
  }

  @Test
  public void testFindByIdSuccessNotFound() {
    when(bookRepository.findById(any())).thenReturn(Optional.empty());

    Book result = bookService.findById(1L);
    Assert.assertNull(result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByIdValidationException() {
    bookService.findById(null);
  }

  @Test
  public void testDeleteByIdSuccess() {
    bookService.deleteById(book.getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdNotFound() {
    EmptyResultDataAccessException exception = new EmptyResultDataAccessException(0);
    doThrow(exception).when(bookRepository).deleteById(any());

    bookService.deleteById(book.getId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdValidationException() {
    bookService.deleteById(null);
  }

  @Test
  public void testFindByTitleSuccess() {
    when(bookRepository.findByTitle(book.getTitle())).thenReturn(Collections.singletonList(book));

    List<Book> result = bookService.findByTitle(book.getTitle());

    Assert.assertNotNull(result);
    Assert.assertEquals(1, result.size());
    Book actual = result.get(0);
    Assert.assertEquals(book.getId(), actual.getId());
    Assert.assertEquals(book.getTitle(), actual.getTitle());
    Assert.assertEquals(book.getAuthors(), actual.getAuthors());
    Assert.assertEquals(book.getPublished(), actual.getPublished());
  }

  @Test
  public void testFindByTitleSuccessNotFound() {
    when(bookRepository.findByTitle(any())).thenReturn(Collections.emptyList());
    List<Book> result = bookService.findByTitle("Spring");
    Assert.assertNotNull(result);
    Assert.assertTrue(result.isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByLastNameValidationException() {
    bookService.findByTitle(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByLastNameValidationException2() {
    bookService.findByTitle("");
  }

}