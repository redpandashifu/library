package org.example.library.repositories;

import java.util.List;
import org.example.library.entities.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
  List<Reader> findByLastName(String lastName);
}
