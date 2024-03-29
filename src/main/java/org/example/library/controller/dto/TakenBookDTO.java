package org.example.library.controller.dto;

import java.time.LocalDate;

public class TakenBookDTO {
  private final Long id;
  private final Long readerId;
  private final Long bookId;
  private final LocalDate dateFrom;
  private final LocalDate dateTo;

  public TakenBookDTO(Long id, Long readerId, Long bookId, LocalDate dateFrom, LocalDate dateTo) {
    this.id = id;
    this.readerId = readerId;
    this.bookId = bookId;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  public Long getId() {
    return id;
  }

  public Long getReaderId() {
    return readerId;
  }

  public Long getBookId() {
    return bookId;
  }

  public LocalDate getDateFrom() {
    return dateFrom;
  }

  public LocalDate getDateTo() {
    return dateTo;
  }

  @Override
  public String toString() {
    return "TakenBookDTO{" +
        "id=" + id +
        ", readerId=" + readerId +
        ", bookId=" + bookId +
        ", dateFrom=" + dateFrom +
        ", dateTo=" + dateTo +
        '}';
  }
}
