package com.refresher.repository;

import com.refresher.domain.Student;
import com.refresher.dto.StudentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT new com.refresher.dto.StudentDto(student) FROM Student student")
    List<StudentDto> selectAll();

    @Query(
            "SELECT new com.refresher.dto.StudentDto(student) FROM Student student WHERE student.id=?1")
    Optional<StudentDto> selectStudentById(final Long id);

    @Query(
            "SELECT new com.refresher.dto.StudentDto(student) FROM Student student WHERE student.firstName "
                    + "LIKE ?1 OR student.lastName LIKE ?1")
    List<StudentDto> selectStudentByName(final String name);
}
