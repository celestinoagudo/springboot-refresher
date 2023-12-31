package com.refresher.repository;

import com.refresher.domain.Student;
import com.refresher.dto.StudentDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface StudentRepository extends JpaRepository<Student, Long> {

  @Transactional(readOnly = true)
  @Query("SELECT new com.refresher.dto.StudentDto(student) FROM Student student")
  List<StudentDto> selectAll();

  @Query(
      "SELECT new com.refresher.dto.StudentDto(student) FROM Student student WHERE student.id=?1")
  Optional<StudentDto> selectStudentById(final Long id);

  @Transactional(readOnly = true)
  @Query(
      "SELECT new com.refresher.dto.StudentDto(student) FROM Student student WHERE student.firstName "
          + "LIKE ?1 OR student.lastName LIKE ?1")
  List<StudentDto> selectStudentByName(final String name);
}
