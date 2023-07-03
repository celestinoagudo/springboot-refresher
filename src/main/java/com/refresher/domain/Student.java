package com.refresher.domain;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import com.refresher.exception.UniversityException;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity(name = "Student")
@Table(
    name = "student",
    uniqueConstraints = {@UniqueConstraint(name = "student_email_unique", columnNames = "email")})
public class Student {

  @Column(name = "age", nullable = false)
  private Integer age;

  @OneToMany(mappedBy = "student", cascade = ALL, orphanRemoval = true)
  private List<Book> books;

  @Column(name = "email", columnDefinition = "TEXT", nullable = false)
  private String email;

  @OneToMany(mappedBy = "student", cascade = ALL, orphanRemoval = true)
  private List<Enrollment> enrollments;

  @Column(name = "first_name", columnDefinition = "TEXT", nullable = false)
  private String firstName;

  @Id
  @SequenceGenerator(
      name = "student_sequence",
      sequenceName = "student_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = SEQUENCE, generator = "student_sequence")
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "last_name", columnDefinition = "TEXT", nullable = false)
  private String lastName;

  @OneToOne(mappedBy = "student", orphanRemoval = true, cascade = ALL)
  private StudentCard studentCard;

  public Student() {}

  public Student(
      final String firstName, final String lastName, final String email, final Integer age) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.age = age;
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

  public List<Enrollment> getEnrollments() {
    return enrollments;
  }

  public void setEnrollments(final List<Enrollment> enrollments) {
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
    studentCard.setStudent(this);
    this.studentCard = studentCard;
  }

  public void addBook(final Book book) {
    if (isNull(books)) {
      books = emptyList();
    }
    if (!books.contains(book)) {
      book.setStudent(this);
      books.add(book);
    }
  }

  public void enroll(final Enrollment enrollment) {
    if (isNull(enrollments)) {
      enrollments = emptyList();
    }
    if (!enrollments.contains(enrollment)) {
      enrollments.add(enrollment);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, firstName, id, lastName);
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    final Student other = (Student) object;
    return Objects.equals(email, other.email)
        && Objects.equals(firstName, other.firstName)
        && Objects.equals(id, other.id)
        && Objects.equals(lastName, other.lastName);
  }

  @Override
  public String toString() {
    return ("Student{age=%d, books=%s, email=%s, enrollments=%s, "
            + "firstName=%s, id=%d, lastName=%s, studentCard=%s}")
        .formatted(age, books, email, enrollments, firstName, id, lastName, studentCard);
  }

  public void removeBook(final Book book) {
    if (isNull(books) || isEmpty(books)) {
      throw new UniversityException(
          "Cannot remove from an empty list of books", new UnsupportedOperationException());
    }
    if (books.contains(book)) {
      book.setStudent(null);
      books.remove(book);
    }
  }

  public void unenroll(final Enrollment enrollment) {
    enrollment.setStudent(null);
    enrollments.remove(enrollment);
  }
}
