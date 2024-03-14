package org.example.library.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BOOK",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"title", "published"})})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Author> authors;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer published;

    public Book() {}

    public Book(String title, List<Author> authors, int published) {
        this.title = title;
        this.authors = authors;
        this.published = published;
    }

    public Book(Book other) {
        this.title = other.title;
        this.authors = other.authors;
        this.published = other.published;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPublished(Integer published) {
        this.published = published;
    }

    public Integer getPublished() {
        return published;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
