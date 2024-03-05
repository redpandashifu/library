package org.example.library.controller.dto;

import java.time.LocalDate;
import org.example.library.entities.Gender;

public class ReaderDTO {

  private final Long id;
  private final String firstName;
  private final String middleName;
  private final String lastName;
  private final Gender gender;
  private final LocalDate birthday;

  public ReaderDTO(long id, String firstName, String middleName, String lastName, Gender gender,
      LocalDate birthday) {
    this.id = id;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.gender = gender;
    this.birthday = birthday;
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public Gender getGender() {
    return gender;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  @Override
  public String toString() {
    return "ReaderDTO{" +
        "id=" + id +
        ", firstName='" + firstName + '\'' +
        ", middleName='" + middleName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", gender=" + gender +
        ", birthday=" + birthday +
        '}';
  }
}
