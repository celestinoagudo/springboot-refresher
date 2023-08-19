package com.refresher.services;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.refresher.domain.Student;
import com.refresher.domain.StudentCard;
import com.refresher.dto.BookDto;
import com.refresher.dto.StudentDto;
import com.refresher.exception.UniversityException;
import com.refresher.repository.CourseRepository;
import com.refresher.repository.StudentRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class StudentServiceTest {

  private StudentService unitUnderTest;

  @Mock private CourseRepository mockCourseRepository;

  @Mock private StudentRepository mockStudentRepository;
  private Faker faker;

  @BeforeEach
  void setup() {
    faker = new Faker();
    unitUnderTest = new StudentService(mockStudentRepository, mockCourseRepository, 15);
  }

  @Test
  @DisplayName("It should not add book to student when student id is not valid")
  void itShouldNotAddBookToStudent() {
    when(mockStudentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    assertAll(
        () -> {
          assertThrows(UniversityException.class, () -> unitUnderTest.addBooks(2L, getBooks()));
        },
        () -> {
          Mockito.verify(mockStudentRepository, Mockito.never()).save(Mockito.any(Student.class));
        });
  }

  @Test
  @DisplayName("It should update student")
  void itShouldUpdateStudent() {
    final var studentDto = new StudentDto();
    final var studentCard = new StudentCard();
    studentCard.setCardNumber(faker.number().digits(15));
    studentDto.setStudentCard(studentCard);
    studentDto.setAge(faker.number().numberBetween(18, 50));
    studentDto.setEmail("faker@email.com");
    studentDto.setLastName(faker.name().lastName());
    studentDto.setFirstName(faker.name().firstName());
    studentDto.setId(1L);
    final var studentFound =
        new Student(
            faker.name().firstName(),
            faker.name().lastName(),
            "random@email.com",
            faker.number().numberBetween(18, 50));
    studentFound.setEnrollments(Collections.emptyList());
    when(mockStudentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(studentFound));
    unitUnderTest.addOrUpdateStudent(studentDto);
    Mockito.verify(mockStudentRepository).save(Mockito.any(Student.class));
  }

  private List<BookDto> getBooks() {
    return List.of(
        new BookDto(now(), faker.book().title()),
        new BookDto(now(), faker.book().title()),
        new BookDto(now(), faker.book().title()));
  }
}
