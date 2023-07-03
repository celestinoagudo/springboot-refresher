package com.refresher.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EnrollmentId implements Serializable {

  @Serial private static final long serialVersionUID = 7146991996557268981L;

  @Column(name = "course_id")
  private Long courseId;

  @Column(name = "student_id")
  private Long studentId;

  public EnrollmentId() {}

  public EnrollmentId(final Long courseId, final Long studentId) {
    this.courseId = courseId;
    this.studentId = studentId;
  }

  public Long getCourseId() {
    return courseId;
  }

  public void setCourseId(final Long courseId) {
    this.courseId = courseId;
  }

  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(final Long studentId) {
    this.studentId = studentId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseId, studentId);
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
    final EnrollmentId other = (EnrollmentId) object;
    return Objects.equals(courseId, other.courseId) && Objects.equals(studentId, other.studentId);
  }

  @Override
  public String toString() {
    return "EnrollmentId{courseId=%d, studentId=%d}".formatted(courseId, studentId);
  }
}
