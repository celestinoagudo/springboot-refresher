package com.refresher.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.refresher.domain.Course;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class CourseDto {

    private String department;
    private Long id;
    private String name;

    public CourseDto() {}

    public CourseDto(final String department, final Long id, final String name) {
        this.department = department;
        this.id = id;
        this.name = name;
    }

    public CourseDto(final Course course) {
        this.department = course.getDepartment();
        this.name = course.getName();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(final String department) {
        this.department = department;
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
        return Objects.hash(department, name);
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
        final CourseDto other = (CourseDto) object;
        return Objects.equals(department, other.department) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "CourseDto{department=%s, id=%d, name=%s}".formatted(department, id, name);
    }
}
