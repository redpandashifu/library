package org.example.library.controller.converter;

import java.util.function.Function;
import org.example.library.controller.dto.AuthorDTO;
import org.example.library.entities.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorConverter implements Function<Author, AuthorDTO> {

  public AuthorDTO apply(Author entity) {
    return new AuthorDTO(entity.getId(), entity.getFirstName(), entity.getLastName());
  }
}
