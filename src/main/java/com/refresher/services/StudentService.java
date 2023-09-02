package com.refresher.services;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import com.github.javafaker.Faker;
import com.refresher.domain.*;
import com.refresher.dto.BookDto;
import com.refresher.dto.CourseDto;
import com.refresher.dto.EnrollmentDto;
import com.refresher.dto.StudentDto;
import com.refresher.exception.UniversityException;
import com.refresher.repository.CourseRepository;
import com.refresher.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

  private final Logger logger = getLogger(StudentService.class);
  private final Integer numberOfCharacters;
  private final CourseRepository courseRepository;
  private final StudentRepository studentRepository;

  @Autowired
  public StudentService(
      final StudentRepository studentRepository,
      final CourseRepository courseRepository,
      final @Value("${app.card-number.length}") Integer numberOfCharacters) {
    this.studentRepository = studentRepository;
    this.courseRepository = courseRepository;
    this.numberOfCharacters = numberOfCharacters;
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "pagedStudentTransferObjects", allEntries = true),
        @CacheEvict(value = "studentTransferObjectById", key = "{#id}"),
        @CacheEvict(value = "studentTransferObjectsByName", allEntries = true),
        @CacheEvict(value = "studentTransferObjects", allEntries = true),
      })
  public StudentDto addBooksTo(final Long id, final List<BookDto> bookDataTransferObjects) {
    logger.info("Adding books...");
    var studentWithMatchingId = getStudentById(id);
    final List<Book> bookEntities = transformToBookEntities().apply(bookDataTransferObjects);
    addBooksTo(studentWithMatchingId, bookEntities);
    studentRepository.save(studentWithMatchingId);
    return new StudentDto(studentWithMatchingId);
  }

  private Function<List<BookDto>, List<Book>> transformToBookEntities() {
    return bookDataTransferObjects -> {
      final Function<BookDto, Book> toBookEntity =
          dataTransferObject -> new Book(dataTransferObject.getName(), now());
      return bookDataTransferObjects.stream().map(toBookEntity).toList();
    };
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "pagedStudentTransferObjects", allEntries = true),
        @CacheEvict(
            value = "studentTransferObjectById",
            key = "{#studentDataTransferObject.getId()}"),
        @CacheEvict(value = "studentTransferObjectsByName", allEntries = true),
        @CacheEvict(value = "studentTransferObjects", allEntries = true),
      })
  public StudentDto addOrUpdateStudent(final StudentDto studentDataTransferObject) {
    return isNull(studentDataTransferObject.getId())
        ? addStudent(studentDataTransferObject)
        : updateStudent(studentDataTransferObject);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "pagedStudentTransferObjects", allEntries = true),
        @CacheEvict(value = "studentTransferObjectById", key = "{#studentId}"),
        @CacheEvict(value = "studentTransferObjectsByName", allEntries = true),
        @CacheEvict(value = "studentTransferObjects", allEntries = true),
      })
  public StudentDto enrollToCourse(
      final Long studentId, final List<CourseDto> courseDataTransferObjects) {
    logger.info("Enrolling student to course...");
    var studentWithMatchingId = getStudentById(studentId);
    final List<Course> validatedCourses =
        validateSubmittedCoursesAndMapToEntities(courseDataTransferObjects);
    addEnrollmentsTo(studentWithMatchingId, validatedCourses);
    return new StudentDto(studentWithMatchingId);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "pagedStudentTransferObjects", allEntries = true),
        @CacheEvict(value = "studentTransferObjectById", key = "{#studentId}"),
        @CacheEvict(value = "studentTransferObjectsByName", allEntries = true),
        @CacheEvict(value = "studentTransferObjects", allEntries = true),
      })
  public StudentDto removeStudent(final Long studentId) {
    logger.info("Removing student...");
    var studentDataTransferObject = new StudentDto(getStudentById(studentId));
    studentRepository.deleteById(studentId);
    return studentDataTransferObject;
  }

  @Cacheable(
      value = "pagedStudentTransferObjects",
      key = "{#page, #itemsPerPage}",
      condition = "@environment.getProperty('app.enable-cache')",
      unless = "#result==null OR #result.isEmpty()")
  public List<StudentDto> getPagedStudentRecords(final int page, final int numberOfItems) {
    logger.info("Getting student with pagination page {{}}, items {{}}", page, numberOfItems);
    final PageRequest request = PageRequest.of(page, numberOfItems);
    final Function<Student, StudentDto> toDataTransferObject = StudentDto::new;
    return studentRepository.findAll(request).stream().map(toDataTransferObject).toList();
  }

  @Cacheable(
      value = "studentTransferObjectById",
      key = "{#id}",
      condition = "@environment.getProperty('app.enable-cache')",
      unless = "#result==null")
  public StudentDto getStudentDtoById(final Long id) {
    final Supplier<UniversityException> exceptionForNonExistence =
        () -> {
          var message = "Student with id '%d' is not found".formatted(id);
          return new UniversityException(message, new IllegalArgumentException(message));
        };
    return studentRepository.selectStudentById(id).orElseThrow(exceptionForNonExistence);
  }

  @Cacheable(
      value = "studentTransferObjectsByName",
      key = "{#name}",
      condition = "@environment.getProperty('app.enable-cache')",
      unless = "#result==null OR #result.isEmpty()")
  public List<StudentDto> getStudentDtoByName(final String name) {
    return studentRepository.selectStudentByName(name);
  }

  @Cacheable(
      value = "studentTransferObjects",
      condition = "@environment.getProperty('app.enable-cache')",
      unless = "#result==null OR #result.isEmpty()")
  public List<StudentDto> getStudents() {
    return studentRepository.selectAll();
  }

  private StudentDto addStudent(final StudentDto studentDataTransferObject) {
    logger.info("Adding student...");
    var studentWithInitialFieldsSet = setInitialFieldsAndReturn(studentDataTransferObject);
    addBooksTo(studentWithInitialFieldsSet, studentDataTransferObject.getBooks());
    var persistedStudent = studentRepository.save(studentWithInitialFieldsSet);
    var validatedCourses = validateCoursesInEnrollments(studentDataTransferObject.getEnrollments());
    addEnrollmentsTo(persistedStudent, validatedCourses);
    studentDataTransferObject.setId(persistedStudent.getId());
    return studentDataTransferObject;
  }

  private Student setInitialFieldsAndReturn(final StudentDto studentDataTransferObject) {
    var student =
        new Student(
            studentDataTransferObject.getFirstName(),
            studentDataTransferObject.getLastName(),
            studentDataTransferObject.getEmail(),
            studentDataTransferObject.getAge());
    var studentCard = new StudentCard(new Faker().number().digits(numberOfCharacters));
    student.setStudentCard(studentCard);
    return student;
  }

  private StudentDto updateStudent(final StudentDto studentDataTransferObject) {
    logger.info("Updating student...");
    var studentWithMatchingId = getStudentById(studentDataTransferObject.getId());
    setInitialFields(studentDataTransferObject, studentWithMatchingId);
    updateBooks(studentDataTransferObject.getBooks(), studentWithMatchingId);
    updateEnrollments(studentDataTransferObject.getEnrollments(), studentWithMatchingId);
    studentRepository.save(studentWithMatchingId);
    return new StudentDto(studentWithMatchingId);
  }

  private void updateEnrollments(
      final List<EnrollmentDto> enrollmentDataTransferObjects, final Student student) {
    if (isNotEmpty(enrollmentDataTransferObjects)) {
      var existingEnrollments = new ArrayList<>(student.getEnrollments());
      var finalEnrollments = new ArrayList<>(enrollmentDataTransferObjects);
      existingEnrollments.forEach(
          unenrollStudentIfNotValidAndPrepareFinalEnrollments(
              enrollmentDataTransferObjects, student, finalEnrollments));
      final List<Course> validatedCourses = validateCoursesInEnrollments(finalEnrollments);
      addEnrollmentsTo(student, validatedCourses);
    }
  }

  private Consumer<Enrollment> unenrollStudentIfNotValidAndPrepareFinalEnrollments(
      final List<EnrollmentDto> submittedEnrollments,
      final Student student,
      final List<EnrollmentDto> finalEnrollments) {
    return existingEnrollment -> {
      var existingEnrollmentAsDataTransferObject = new EnrollmentDto(existingEnrollment);
      if (!submittedEnrollments.contains(existingEnrollmentAsDataTransferObject)) {
        student.unenroll(existingEnrollment);
        return;
      }
      finalEnrollments.remove(existingEnrollmentAsDataTransferObject);
    };
  }

  private void updateBooks(final List<Book> books, final Student student) {
    if (isNotEmpty(books)) {
      var existingBooks = new ArrayList<>(student.getBooks());
      existingBooks.forEach(student::removeBook);
      addBooksTo(student, books);
    }
  }

  private void setInitialFields(final StudentDto studentDataTransferObject, final Student student) {
    student.setAge(studentDataTransferObject.getAge());
    student.setEmail(studentDataTransferObject.getEmail());
    student.setFirstName(studentDataTransferObject.getFirstName());
    student.setLastName(studentDataTransferObject.getLastName());
  }

  private void addEnrollmentsTo(final Student student, final List<Course> courses) {
    if (isNotEmpty(courses)) {
      courses.forEach(enrollStudent(student));
      studentRepository.save(student);
    }
  }

  private Consumer<Course> enrollStudent(final Student student) {
    return course -> {
      var enrollmentId = new EnrollmentId(student.getId(), course.getId());
      var enrollment = new Enrollment(course, now(), enrollmentId, student);
      student.enroll(enrollment);
    };
  }

  private List<Course> validateSubmittedCoursesAndMapToEntities(
      List<CourseDto> courseDataTransferObjects) {
    return courseDataTransferObjects.stream().map(toCourseIfValid()).toList();
  }

  private Function<CourseDto, Course> toCourseIfValid() {
    return courseDto -> {
      final Supplier<UniversityException> exceptionForInvalidCourse =
          () ->
              new UniversityException(
                  "Course is invalid '%s'".formatted(courseDto), new IllegalArgumentException());
      return courseRepository
          .selectCourseByNameAndDepartment(courseDto.getName(), courseDto.getDepartment())
          .orElseThrow(exceptionForInvalidCourse);
    };
  }

  private List<Course> validateCoursesInEnrollments(
      final List<EnrollmentDto> enrollmentsDataTransferObjects) {
    final Function<EnrollmentDto, CourseDto> toCourseEntity = EnrollmentDto::getCourse;
    if (isNotEmpty(enrollmentsDataTransferObjects)) {
      return validateSubmittedCoursesAndMapToEntities(
          enrollmentsDataTransferObjects.stream().map(toCourseEntity).toList());
    }
    return emptyList();
  }

  private Student getStudentById(final Long id) {
    final Supplier<UniversityException> exceptionForNonExistence =
        () ->
            new UniversityException(
                "Student with id '%d' is not found".formatted(id), new IllegalArgumentException());
    return studentRepository.findById(id).orElseThrow(exceptionForNonExistence);
  }

  private void addBooksTo(final Student student, final List<Book> books) {
    Consumer<Book> setBookCreatedAtAndAddToStudent =
        book -> {
          book.setCreatedAt(now());
          student.addBook(book);
        };
    if (isNotEmpty(books)) {
      books.forEach(setBookCreatedAtAndAddToStudent);
    }
  }
}
