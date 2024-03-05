package org.example.library.controller.dto;

import java.util.Set;

public class BookDTO {

  private Long id;
  private String title;
  private Set<Long> authorIds;
  private Integer published;

  public BookDTO(Long id, String title, Set<Long> authorIds, Integer published) {
    this.id = id;
    this.title = title;
    this.authorIds = authorIds;
    this.published = published;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Integer getPublished() {
    return published;
  }

  public Set<Long> getAuthorIds() {
    return authorIds;
  }

  @Override
  public String toString() {
    return "BookDTO{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", authorIds=" + authorIds +
        ", published=" + published +
        '}';
  }
}
