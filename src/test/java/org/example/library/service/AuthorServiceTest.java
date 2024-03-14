package org.example.library.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.example.library.entities.Author;
import org.example.library.repositories.AuthorRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

public class AuthorServiceTest {
  private AuthorService authorService;
  private AuthorRepository authorRepository;

  private Author author;

  @Before
  public void setUp() {
    authorRepository = Mockito.mock(AuthorRepository.class);
    authorService = new AuthorService(authorRepository);

    author = new Author("Alex", "Pushkin");
    author.setId(99L);
  }

  @Test
  public void testCreateAuthorSuccess() {
    when(authorRepository.saveAndFlush(any())).thenReturn(author);

    Author result = authorService.createAuthor(author.getFirstName(), author.getLastName());

    Assert.assertNotNull(result);
    Assert.assertEquals(author.getFirstName(), result.getFirstName());
    Assert.assertEquals(author.getLastName(), result.getLastName());
  }

  @Test
  public void testCreateAuthorValidationException() {
    Author author0 = new Author(null, "Pushkin");
    Author author1 = new Author("", "Pushkin");
    Author author2 = new Author("Alex", null);
    Author author3 = new Author("Alex", "");

    List<Author> authors = Arrays.asList(author0, author1, author2, author3);
    for (Author author : authors) {
      try {
        authorService.createAuthor(author.getFirstName(), author.getLastName());
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testCreateAuthorIntegrityException() {
    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(authorRepository).saveAndFlush(any());

    try {
      authorService.createAuthor("Alex", "Pushkin");
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testUpdateAuthorSuccess() {
    when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
    when(authorRepository.saveAndFlush(any())).thenReturn(author);

    Author result = authorService.updateAuthor(author.getId(), author.getFirstName(),
        author.getLastName());

    Assert.assertNotNull(result);
    Assert.assertEquals(author.getId(), result.getId());
    Assert.assertEquals(author.getFirstName(), result.getFirstName());
    Assert.assertEquals(author.getLastName(), result.getLastName());
  }

  @Test
  public void testUpdateAuthorValidationException() {
    Author author0 = new Author(null, "Pushkin");
    Author author1 = new Author("", "Pushkin");
    Author author2 = new Author("Alex", null);
    Author author3 = new Author("Alex", "");

    List<Author> authors = Arrays.asList(author0, author1, author2, author3);
    for (Author author : authors) {
      try {
        authorService.updateAuthor(99L, author.getFirstName(), author.getLastName());
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testUpdateAuthorIntegrityException() {
    when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));

    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(authorRepository).saveAndFlush(any());

    try {
      authorService.updateAuthor(author.getId(), "Alexxx", "Pushkin");
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testFindByIdSuccess() {
    when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));

    Author result = authorService.findById(author.getId());

    Assert.assertNotNull(result);
    Assert.assertEquals(author.getId(), result.getId());
    Assert.assertEquals(author.getFirstName(), result.getFirstName());
    Assert.assertEquals(author.getLastName(), result.getLastName());
  }

  @Test
  public void testFindByIdSuccessNotFound() {
    when(authorRepository.findById(any())).thenReturn(Optional.empty());

    Author result = authorService.findById(1L);
    Assert.assertNull(result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByIdValidationException() {
    authorService.findById(null);
  }

  @Test
  public void testDeleteByIdSuccess() {
    authorService.deleteById(1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdNotFound() {
    EmptyResultDataAccessException exception = new EmptyResultDataAccessException(0);
    doThrow(exception).when(authorRepository).deleteById(any());

    authorService.deleteById(1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdValidationException() {
    authorService.deleteById(null);
  }

  @Test
  public void testFindByLastNameSuccess() {
    when(authorRepository.findByLastName(author.getLastName())).thenReturn(
        Collections.singletonList(author));

    List<Author> result = authorService.findByLastName(author.getLastName());

    Assert.assertNotNull(result);
    Assert.assertEquals(1, result.size());
    Author actual = result.get(0);
    Assert.assertEquals(author.getId(), actual.getId());
    Assert.assertEquals(author.getFirstName(), actual.getFirstName());
    Assert.assertEquals(author.getLastName(), actual.getLastName());
  }

  @Test
  public void testFindByLastNameSuccessNotFound() {
    when(authorRepository.findByLastName(any())).thenReturn(Collections.emptyList());

    List<Author> result = authorService.findByLastName("Alex");
    Assert.assertNotNull(result);
    Assert.assertEquals(0, result.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByLastNameValidationException() {
    authorService.findByLastName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByLastNameValidationException2() {
    authorService.findByLastName("");
  }
}