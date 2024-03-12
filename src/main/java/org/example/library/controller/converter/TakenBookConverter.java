package org.example.library.controller.converter;

import java.util.function.Function;
import org.example.library.controller.dto.TakenBookDTO;
import org.example.library.entities.TakenBook;
import org.springframework.stereotype.Component;

@Component
public class TakenBookConverter implements Function<TakenBook, TakenBookDTO> {

  public TakenBookDTO apply(TakenBook entity) {
    return new TakenBookDTO(entity.getId(),
        entity.getReader().getId(),
        entity.getBook().getId(),
        entity.getDateFrom(),
        entity.getDateTo());
  }
}
