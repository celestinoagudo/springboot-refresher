package com.refresher.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "Book")
@Table(name = "book")
@JsonInclude(NON_NULL)
public class Book {

  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
  private LocalDateTime createdAt;

  @Id
  @SequenceGenerator(name = "book_sequence", sequenceName = "book_sequence", allocationSize = 1)
  @GeneratedValue(strategy = SEQUENCE, generator = "book_sequence")
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, columnDefinition = "TEXT")
  private String name;

  @ManyToOne(cascade = CascadeType.ALL, fetch = EAGER)
  @JoinColumn(
      name = "student_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "student_id_fkey"))
  private Student student;

  public Book() {}

  public Book(
      final LocalDateTime createdAt, final Long id, final String name, final Student student) {
    this.createdAt = createdAt;
    this.id = id;
    this.name = name;
    this.student = student;
  }

  public Book(final String name, final LocalDateTime createdAt) {
    this.name = name;
    this.createdAt = createdAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(final Student student) {
    this.student = student;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id);
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
    final Book other = (Book) object;
    return Objects.equals(name, other.name) && Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return "Book{createdAt=%s, id=%d, name=%s, student=%s}".formatted(createdAt, id, name, student);
  }
}
