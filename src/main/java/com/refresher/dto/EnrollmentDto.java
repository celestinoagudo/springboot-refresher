package com.refresher.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.refresher.domain.Enrollment;

@JsonInclude(NON_NULL)
public class EnrollmentDto {

  private CourseDto course;

  public EnrollmentDto() {}

  public EnrollmentDto(final Enrollment enrollment) {
    course = new CourseDto(enrollment.getCourse());
  }

  public CourseDto getCourse() {
    return course;
  }

  public void setCourse(final CourseDto course) {
    this.course = course;
  }

  @Override
  public String toString() {
    return "EnrollmentDto{course=%s}".formatted(course);
  }
}
