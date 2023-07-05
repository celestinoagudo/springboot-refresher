package com.refresher.repository;

import com.refresher.domain.Course;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CourseRepository extends JpaRepository<Course, Long> {

  @Transactional(readOnly = true)
  @Query(
      "SELECT course FROM Course course WHERE course.name=:name AND course.department=:department")
  Optional<Course> selectCourseByNameAndDepartment(final String name, final String department);
}
