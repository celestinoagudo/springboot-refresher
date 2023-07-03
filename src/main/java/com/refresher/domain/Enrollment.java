package com.refresher.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "Enrollment")
@Table(name = "enrollment")
public class Enrollment {

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "enrollment_course_id_fkey"))
    private Course course;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @EmbeddedId
    private EnrollmentId enrollmentId;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "enrollment_student_id_fkey"))
    private Student student;

    public Enrollment() {}

    public Enrollment(
            final Course course,
            final LocalDateTime createdAt,
            final EnrollmentId enrollmentId,
            final Student student) {
        this.course = course;
        this.createdAt = createdAt;
        this.enrollmentId = enrollmentId;
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(final Course course) {
        this.course = course;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public EnrollmentId getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(final EnrollmentId enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(final Student student) {
        this.student = student;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
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
        final Enrollment other = (Enrollment) object;
        return Objects.equals(enrollmentId, other.enrollmentId);
    }

    @Override
    public String toString() {
        return "Enrollment{course=%s, createdAt=%s, enrollmentId=%s, student=%s}"
                .formatted(course, createdAt, enrollmentId, student);
    }
}
