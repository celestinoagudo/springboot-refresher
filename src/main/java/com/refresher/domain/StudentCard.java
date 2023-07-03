package com.refresher.domain;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.*;
import java.util.Objects;

@Entity(name = "StudentCard")
@Table(name = "student_card")
public class StudentCard {

  @Column(name = "card_number", nullable = false, columnDefinition = "TEXT", length = 15)
  private String cardNumber;

  @Id
  @SequenceGenerator(
      name = "student_card_sequence",
      sequenceName = "student_card_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = SEQUENCE, generator = "student_card_sequence")
  @Column(name = "id", updatable = false)
  private Long id;

  @OneToOne(cascade = CascadeType.ALL, fetch = EAGER)
  @JoinColumn(
      name = "student_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "student_id_fkey"))
  private Student student;

  public StudentCard(final String cardNumber, final Long id, final Student student) {
    this.cardNumber = cardNumber;
    this.id = id;
    this.student = student;
  }

  public StudentCard(final String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public StudentCard() {}

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(final String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(final Student student) {
    this.student = student;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardNumber, id);
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
    final StudentCard other = (StudentCard) object;
    return Objects.equals(cardNumber, other.cardNumber) && Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return "StudentCard{cardNumber=%s, id=%d, student=%s}".formatted(cardNumber, id, student);
  }
}
