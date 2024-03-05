package org.example.library.controller.converter;

import java.util.function.Function;
import java.util.stream.Collectors;
import org.example.library.controller.dto.BookDTO;
import org.example.library.entities.Author;
import org.example.library.entities.Book;
import org.springframework.stereotype.Component;

@Component
public class BookConverter implements Function<Book, BookDTO> {

  public BookDTO apply(Book entity) {
    return new BookDTO(entity.getId(),
        entity.getTitle(),
        entity.getAuthors().stream().map(Author::getId).collect(Collectors.toSet()),
        entity.getPublished());
  }
}