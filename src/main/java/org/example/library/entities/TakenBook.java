package org.example.library.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TAKEN_BOOK")
public class TakenBook {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "reader_id", nullable = false)
  private Reader reader;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(nullable = false)
  private LocalDate dateFrom;

  @Column
  private LocalDate dateTo;

  public TakenBook() {
  }

  public TakenBook(Reader reader, Book book, LocalDate dateFrom, LocalDate dateTo) {
    this.reader = reader;
    this.book = book;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TakenBook(TakenBook other) {
    this.reader = other.reader;
    this.book = other.book;
    this.dateFrom = other.dateFrom;
    this.dateTo = other.dateTo;
  }

  public Long getId() {
    return id;
  }

  public Reader getReader() {
    return reader;
  }

  public void setReader(Reader reader) {
    this.reader = reader;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }

  public LocalDate getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(LocalDate dateFrom) {
    this.dateFrom = dateFrom;
  }

  public LocalDate getDateTo() {
    return dateTo;
  }

  public void setDateTo(LocalDate dateTo) {
    this.dateTo = dateTo;
  }
}
