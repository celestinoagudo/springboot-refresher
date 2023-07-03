package com.refresher.repository;

import com.refresher.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query(
            "SELECT course FROM Course course WHERE course.name=:name AND course.department=:department")
    Optional<Course> selectCourseByNameAndDepartment(final String name, final String department);
}
