package org.example.library.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.example.library.entities.Gender;
import org.example.library.entities.Reader;
import org.example.library.repositories.ReaderRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

public class ReaderServiceTest {

  private ReaderService readerService;
  private ReaderRepository readerRepository;

  private Reader reader;

  @Before
  public void setUp() {
    readerRepository = Mockito.mock((ReaderRepository.class));
    readerService = new ReaderService(readerRepository);

    reader = new Reader("Max", "Sergeevich", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12));
    reader.setId(99L);
  }

  @Test
  public void testCreateReaderSuccess() {
    when(readerRepository.saveAndFlush(any())).thenReturn(reader);

    Reader result = readerService.createReader(reader.getFirstName(), reader.getMiddleName(),
        reader.getLastName(), reader.getGender(), reader.getBirthday());

    Assert.assertNotNull(result);
    Assert.assertEquals(reader.getFirstName(), result.getFirstName());
    Assert.assertEquals(reader.getMiddleName(), result.getMiddleName());
    Assert.assertEquals(reader.getLastName(), result.getLastName());
    Assert.assertEquals(reader.getGender(), result.getGender());
    Assert.assertEquals(reader.getBirthday(), result.getBirthday());
  }

  @Test
  public void testCreateReaderValidationException() {
    List<Reader> readers = new ArrayList<>();
    //firstName
    readers.add(new Reader(null, "Sergeevich", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    readers.add(new Reader("", "Sergeevich", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    //middleName
    readers.add(new Reader("Max", null, "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    readers.add(new Reader("Max", "", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    //lastName
    readers.add(new Reader("Max", "Sergeevich", null, Gender.MALE, LocalDate.of(1990, 12, 12)));
    readers.add(new Reader("Max", "Sergeevich", "", Gender.MALE, LocalDate.of(1990, 12, 12)));
    //gender
    readers.add(new Reader("Max", "Sergeevich", "Petrov", null, LocalDate.of(1990, 12, 12)));
    //birthDate
    readers.add(new Reader("Max", "Sergeevich", "Petrov", Gender.MALE, null));

    for (Reader reader : readers) {
      try {
        readerService.createReader(reader.getFirstName(), reader.getMiddleName(),
            reader.getLastName(), reader.getGender(), reader.getBirthday());
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testCreateReaderIntegrityException() {
    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(readerRepository).saveAndFlush(any());

    try {
      readerService.createReader("Max", "Sergeevich", "Petrov", Gender.MALE,
          LocalDate.of(1990, 12, 12));
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testUpdateReaderSuccess() {
    when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));
    when(readerRepository.saveAndFlush(any())).thenReturn(reader);

    Reader result = readerService.updateReader(reader.getId(), reader.getFirstName(),
        reader.getMiddleName(),
        reader.getLastName(), reader.getGender(), reader.getBirthday());

    Assert.assertNotNull(result);
    Assert.assertEquals(reader.getId(), result.getId());
    Assert.assertEquals(reader.getFirstName(), result.getFirstName());
    Assert.assertEquals(reader.getMiddleName(), result.getMiddleName());
    Assert.assertEquals(reader.getLastName(), result.getLastName());
    Assert.assertEquals(reader.getGender(), result.getGender());
    Assert.assertEquals(reader.getBirthday(), result.getBirthday());
  }

  @Test
  public void testUpdateReaderValidationException() {
    List<Reader> readers = new ArrayList<>();
    //firstName
    readers.add(new Reader(null, "Sergeevich", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    readers.add(new Reader("", "Sergeevich", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    //middleName
    readers.add(new Reader("Max", null, "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    readers.add(new Reader("Max", "", "Petrov", Gender.MALE, LocalDate.of(1990, 12, 12)));
    //lastName
    readers.add(new Reader("Max", "Sergeevich", null, Gender.MALE, LocalDate.of(1990, 12, 12)));
    readers.add(new Reader("Max", "Sergeevich", "", Gender.MALE, LocalDate.of(1990, 12, 12)));
    //gender
    readers.add(new Reader("Max", "Sergeevich", "Petrov", null, LocalDate.of(1990, 12, 12)));
    //birthDate
    readers.add(new Reader("Max", "Sergeevich", "Petrov", Gender.MALE, null));

    for (Reader reader : readers) {
      try {
        readerService.updateReader(reader.getId(), reader.getFirstName(), reader.getMiddleName(),
            reader.getLastName(), reader.getGender(), reader.getBirthday());
      } catch (IllegalArgumentException ex) {
        continue;
      }
      Assert.fail();
    }
  }

  @Test
  public void testUpdateReaderIntegrityException() {
    when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));

    DataIntegrityViolationException exception = new DataIntegrityViolationException("DIVE");
    doThrow(exception).when(readerRepository).saveAndFlush(any());

    try {
      readerService.updateReader(reader.getId(), "Maxxx", "Sergeevich", "Petrov", Gender.MALE,
          LocalDate.of(1990, 12, 12));
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals(exception.getMessage(), ex.getMessage());
      return;
    }
    Assert.fail();
  }

  @Test
  public void testFindByIdSuccess() {
    when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));

    Reader result = readerService.findById(reader.getId());

    Assert.assertNotNull(result);
    Assert.assertEquals(reader.getId(), result.getId());
    Assert.assertEquals(reader.getFirstName(), result.getFirstName());
    Assert.assertEquals(reader.getMiddleName(), result.getMiddleName());
    Assert.assertEquals(reader.getLastName(), result.getLastName());
    Assert.assertEquals(reader.getGender(), result.getGender());
    Assert.assertEquals(reader.getBirthday(), result.getBirthday());
  }

  @Test
  public void testFindByIdSuccessNotFound() {
    when(readerRepository.findById(any())).thenReturn(Optional.empty());

    Reader result = readerService.findById(1L);
    Assert.assertNull(result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByIdValidationException() {
    readerService.findById(null);
  }

  @Test
  public void testDeleteByIdSuccess() {
    readerService.deleteById(1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdNotFound() {
    EmptyResultDataAccessException exception = new EmptyResultDataAccessException(0);
    doThrow(exception).when(readerRepository).deleteById(any());

    readerService.deleteById(1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteByIdValidationException() {
    readerService.deleteById(null);
  }

  @Test
  public void testFindByLastNameSuccess() {
    Reader reader = new Reader("Max", "Sergeevich", "Petrov", Gender.MALE,
        LocalDate.of(1990, 12, 12));
    reader.setId(99L);
    when(readerRepository.findByLastName(reader.getLastName())).thenReturn(
        Collections.singletonList(reader));

    List<Reader> result = readerService.findByLastName(reader.getLastName());

    Assert.assertNotNull(result);
    Assert.assertEquals(1, result.size());
    Reader actual = result.get(0);
    Assert.assertEquals(reader.getId(), actual.getId());
    Assert.assertEquals(reader.getFirstName(), actual.getFirstName());
    Assert.assertEquals(reader.getMiddleName(), actual.getMiddleName());
    Assert.assertEquals(reader.getLastName(), actual.getLastName());
    Assert.assertEquals(reader.getGender(), actual.getGender());
    Assert.assertEquals(reader.getBirthday(), actual.getBirthday());
  }

  @Test
  public void testFindByLastNameSuccessNotFound() {
    when(readerRepository.findByLastName(any())).thenReturn(Collections.emptyList());
    List<Reader> result = readerService.findByLastName("Alex");
    Assert.assertNotNull(result);
    Assert.assertEquals(0, result.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByLastNameValidationException() {
    readerService.findByLastName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindByLastNameValidationException2() {
    readerService.findByLastName("");
  }
}