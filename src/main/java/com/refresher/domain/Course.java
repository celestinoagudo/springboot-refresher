package com.refresher.domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity(name = "Course")
@Table(name = "course")
public class Course {

    @Column(name = "department", nullable = false, columnDefinition = "TEXT")
    private String department;

    @OneToMany(mappedBy = "course", cascade = ALL)
    private List<Enrollment> enrollments;

    @Id
    @SequenceGenerator(name = "course_sequence", sequenceName = "course_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "course_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    public Course() {}

    public Course(final String department, final List<Enrollment> enrollments, final Long id, final String name) {
        this.department = department;
        this.enrollments = enrollments;
        this.id = id;
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(final String department) {
        this.department = department;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(final List<Enrollment> enrollments) {
        this.enrollments = enrollments;
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

    @Override
    public int hashCode() {
        return Objects.hash(department, id, name);
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
        final Course other = (Course) object;
        return Objects.equals(department, other.department)
                && Objects.equals(id, other.id)
                && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "Course{department=%s, enrollments=%s, id=%d, name=%s}".formatted(department, enrollments, id, name);
    }
}
