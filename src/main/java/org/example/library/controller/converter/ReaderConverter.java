package org.example.library.controller.converter;

import java.util.function.Function;
import org.example.library.controller.dto.ReaderDTO;
import org.example.library.entities.Reader;
import org.springframework.stereotype.Component;

@Component
public class ReaderConverter implements Function<Reader, ReaderDTO> {

  public ReaderDTO apply(Reader entity) {
    return new ReaderDTO(entity.getId(),
        entity.getFirstName(),
        entity.getMiddleName(),
        entity.getLastName(),
        entity.getGender(),
        entity.getBirthday());
  }
}
