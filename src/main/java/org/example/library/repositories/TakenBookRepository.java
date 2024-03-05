package org.example.library.repositories;

import java.time.LocalDate;
import java.util.List;
import org.example.library.entities.TakenBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TakenBookRepository extends JpaRepository<TakenBook, Long> {
  @Query("SELECT t FROM TakenBook t WHERE t.reader.id = ?1 AND t.dateFrom between ?2 and ?3")
  List<TakenBook> findByReaderAndPeriod(long readerId, LocalDate dateFrom, LocalDate dateTo);
}
