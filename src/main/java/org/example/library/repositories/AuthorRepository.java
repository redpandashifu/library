package org.example.library.repositories;

import java.util.List;
import java.util.Set;
import org.example.library.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
  List<Author> findByLastName(String lastName);

  @Query("SELECT a FROM Author a WHERE a.id IN (:ids)")
  List<Author> findByIds(Set<Long> ids);
}
