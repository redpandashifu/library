package org.example.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.library.entities.Gender;
import org.example.library.entities.Reader;
import org.example.library.repositories.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReaderService {
  @Autowired
  private ReaderRepository readerRepository;

  @Transactional
  public Reader createReader(String firstName, String middleName, String lastName, Gender gender,
      LocalDate birthday) {
    try {
      validate(firstName, middleName, lastName, birthday);
      return readerRepository.saveAndFlush(new Reader(firstName, middleName, lastName, gender, birthday));
    } catch (
        DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public Reader updateReader(Long id, String firstName, String middleName, String lastName,
      Gender gender, LocalDate birthday) {
    try {
      if (id == null) {
        throw new IllegalArgumentException("Reader id is not specified");
      }
      Optional<Reader> optionalReader = readerRepository.findById(id);
      if (!optionalReader.isPresent()) {
        throw new NotFoundException("Reader with id=" + id + " doesn't exist");
      }

      validate(firstName, middleName, lastName, birthday);
      Reader reader = optionalReader.get();
      reader.setFirstName(firstName);
      reader.setMiddleName(middleName);
      reader.setLastName(lastName);
      reader.setGender(gender);
      reader.setBirthday(birthday);
      return readerRepository.saveAndFlush(reader);
    } catch (DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public Reader findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Reader id is not specified");
    }
    Optional<Reader> optionalReader = readerRepository.findById(id);
    return optionalReader.orElse(null);
  }

  @Transactional
  public void deleteById(Long id) {
    try {
      if (id == null) {
        throw new IllegalArgumentException("Reader id is not specified");
      }
      readerRepository.deleteById(id);
    } catch (EmptyResultDataAccessException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public List<Reader> findByLastName(String lastName) {
    if (lastName == null || lastName.isEmpty()) {
      throw new IllegalArgumentException("Reader last name is not specified");
    }
    return readerRepository.findByLastName(lastName);
  }

  private void validate(String firstName, String middleName, String lastName, LocalDate birthday) {
    if (firstName == null || firstName.isEmpty()) {
      throw new IllegalArgumentException("Reader first name is null or empty");
    }
    if (middleName == null || middleName.isEmpty()) {
      throw new IllegalArgumentException("Reader middle name is null or empty");
    }
    if (lastName == null || lastName.isEmpty()) {
      throw new IllegalArgumentException("Reader last name is null or empty");
    }
    if (birthday == null) {
      throw new IllegalArgumentException("Reader birthday is not specified");
    }
  }
}
