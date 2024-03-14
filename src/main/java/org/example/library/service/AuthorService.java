package org.example.library.service;

import java.util.List;
import java.util.Optional;
import org.example.library.entities.Author;
import org.example.library.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {
  @Autowired
  private AuthorRepository authorRepository;

  public AuthorService(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  @Transactional
  public Author createAuthor(String firstName, String lastName) {
    try {
      validate(firstName, lastName);
      return authorRepository.saveAndFlush(new Author(firstName, lastName));
    } catch(DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public Author updateAuthor(Long id, String firstName, String lastName) {
    try {
      validate(firstName, lastName);

      if (id == null) {
        throw new IllegalArgumentException("Author id is not specified");
      }
      Optional<Author> optionalAuthor = authorRepository.findById(id);
      if (!optionalAuthor.isPresent()) {
        throw new NotFoundException("Author with id=" + id + " doesn't exist");
      }

      Author author = optionalAuthor.get();
      author.setFirstName(firstName);
      author.setLastName(lastName);
      return authorRepository.saveAndFlush(author);
    } catch(DataIntegrityViolationException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public Author findById(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Author id is not specified");
    }
    Optional<Author> optionalAuthor = authorRepository.findById(id);
    return optionalAuthor.orElse(null);
  }

  @Transactional
  public void deleteById(Long id) {
    try {
      if (id == null) {
        throw new IllegalArgumentException("Author id is not specified");
      }
      authorRepository.deleteById(id);
    } catch (EmptyResultDataAccessException ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  @Transactional
  public List<Author> findByLastName(String lastName) {
    if (lastName == null || lastName.isEmpty()) {
      throw new IllegalArgumentException("Author last name is not specified");
    }
    return authorRepository.findByLastName(lastName);
  }

  private void validate(String firstName, String lastName) {
    if (firstName == null || firstName.isEmpty()) {
      throw new IllegalArgumentException("Author first name is null or empty");
    }
    if (lastName == null || lastName.isEmpty()) {
      throw new IllegalArgumentException("Author last name is null or empty");
    }
  }
}
