package org.example.library;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.example.library.entities.Author;
import org.example.library.entities.Book;
import org.example.library.entities.Gender;
import org.example.library.entities.Reader;
import org.example.library.entities.TakenBook;
import org.example.library.service.AuthorService;
import org.example.library.service.BookService;
import org.example.library.service.ReaderService;
import org.example.library.service.TakenBookService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class TakenBookServiceTest {
  @Autowired
  private BookService bookService;

  @Autowired
  private ReaderService readerService;

  @Autowired
  private AuthorService authorService;

  @Autowired
  private TakenBookService takenBookService;

  @Test
  public void testFindTakenBooksByReaderAndPeriod() {
    Author author = authorService.createAuthor("Astrid", "Lindgren");
    Assert.assertNotNull(author);
    Assert.assertNotNull(author.getId());
    Assert.assertEquals("Astrid", author.getFirstName());
    Assert.assertEquals("Lindgren", author.getLastName());

    Book book = bookService.createBook("Pippi Longstocking", 1945,
        Collections.singleton(author.getId()));
    Assert.assertNotNull(book);
    Assert.assertNotNull(book.getId());
    Assert.assertEquals("Pippi Longstocking", book.getTitle());
    Assert.assertEquals(1945, (long)book.getPublished());

    Reader reader = readerService.createReader("Ivan", "Ivanovich", "Ivanov", Gender.MALE,
        LocalDate.of(2000, 2, 14));
    Assert.assertNotNull(reader);
    Assert.assertNotNull(reader.getId());
    Assert.assertEquals("Ivan", reader.getFirstName());
    Assert.assertEquals("Ivanovich", reader.getMiddleName());
    Assert.assertEquals("Ivanov", reader.getLastName());
    Assert.assertEquals(Gender.MALE, reader.getGender());
    Assert.assertEquals(LocalDate.of(2000, 2, 14), reader.getBirthday());

    TakenBook takenBook = takenBookService.createTakenBook(reader.getId(), book.getId(),
        LocalDate.of(2024, 3, 3), null);
    Assert.assertNotNull(takenBook);
    Assert.assertNotNull(takenBook.getId());
    Assert.assertNotNull(takenBook.getBook());
    Assert.assertEquals(book.getId(), takenBook.getBook().getId());
    Assert.assertNotNull(takenBook.getReader());
    Assert.assertEquals(reader.getId(), takenBook.getReader().getId());
    Assert.assertEquals(LocalDate.of(2024, 3, 3), takenBook.getDateFrom());
    Assert.assertNull(takenBook.getDateTo());

    List<TakenBook> takenBooks = takenBookService.findByPeriod(reader.getId(), LocalDate.of(2024, 3, 1),
        LocalDate.of(2024, 3, 5));
    Assert.assertNotNull(takenBooks);
    Assert.assertEquals(1, takenBooks.size());
    takenBook = takenBooks.get(0);
    Assert.assertNotNull(takenBook);
    Assert.assertNotNull(takenBook.getId());
    Assert.assertNotNull(takenBook.getBook());
    Assert.assertEquals(book.getId(), takenBook.getBook().getId());
    Assert.assertNotNull(takenBook.getReader());
    Assert.assertEquals(reader.getId(), takenBook.getReader().getId());
    Assert.assertEquals(LocalDate.of(2024, 3, 3), takenBook.getDateFrom());
    Assert.assertNull(takenBook.getDateTo());

    takenBooks = takenBookService.findByPeriod(reader.getId(), LocalDate.of(2024, 3, 5),
        LocalDate.of(2024, 3, 10));
    Assert.assertNotNull(takenBooks);
    Assert.assertTrue(takenBooks.isEmpty());
  }
}
