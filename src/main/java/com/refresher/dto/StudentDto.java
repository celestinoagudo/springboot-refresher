package com.refresher.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.refresher.domain.Book;
import com.refresher.domain.Student;
import com.refresher.domain.StudentCard;
import java.util.List;

@JsonInclude(NON_NULL)
@JsonPropertyOrder({
  "id",
  "firstName",
  "lastName",
  "email",
  "age",
  "studentCard",
  "books",
  "enrollments"
})
public class StudentDto {

  private Integer age;

  @JsonIgnoreProperties("student")
  private List<Book> books;

  private String email;
  private List<EnrollmentDto> enrollments;
  private String firstName;
  private Long id;
  private String lastName;

  @JsonIgnoreProperties("student")
  private StudentCard studentCard;

  public StudentDto() {}

  public StudentDto(final Student student) {
    age = student.getAge();
    books = student.getBooks();
    enrollments = student.getEnrollments().stream().map(EnrollmentDto::new).collect(toList());
    firstName = student.getFirstName();
    id = student.getId();
    lastName = student.getLastName();
    studentCard = student.getStudentCard();
    email = student.getEmail();
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(final Integer age) {
    this.age = age;
  }

  public List<Book> getBooks() {
    return books;
  }

  public void setBooks(final List<Book> books) {
    this.books = books;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public List<EnrollmentDto> getEnrollments() {
    return enrollments;
  }

  public void setEnrollments(final List<EnrollmentDto> enrollments) {
    this.enrollments = enrollments;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public StudentCard getStudentCard() {
    return studentCard;
  }

  public void setStudentCard(final StudentCard studentCard) {
    this.studentCard = studentCard;
  }

  @Override
  public String toString() {
    return ("Student{age=%d, books=%s, email=%s, enrollments=%s, "
            + "firstName=%s, id=%d, lastName=%s, studentCard=%s}")
        .formatted(age, books, email, enrollments, firstName, id, lastName, studentCard);
  }
}
