package com.refresher.services;

import com.github.javafaker.Faker;
import com.refresher.domain.*;
import com.refresher.dto.BookDto;
import com.refresher.dto.CourseDto;
import com.refresher.dto.EnrollmentDto;
import com.refresher.dto.StudentDto;
import com.refresher.exception.UniversityException;
import com.refresher.repository.CourseRepository;
import com.refresher.repository.StudentRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class StudentService {

    private final Logger logger = getLogger(StudentService.class);
    private final Integer cardNumberLength;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(
            final StudentRepository studentRepository,
            final CourseRepository courseRepository,
            final @Value("${app.card-number.length}") Integer cardNumberLength) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.cardNumberLength = cardNumberLength;
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "pagedStudentTransferObjects", allEntries = true),
                @CacheEvict(value = "studentTransferObjectById", key = "{#id}"),
                @CacheEvict(value = "studentTransferObjectsByName", allEntries = true),
                @CacheEvict(value = "studentTransferObjects", allEntries = true),
            })
    public StudentDto addBooks(final Long id, final List<BookDto> bookTransferObjects) {
        logger.info("Adding books...");
        var student = getStudentById(id);
        Function<List<BookDto>, List<Book>> transformToEntities = transferObjects -> transferObjects.stream()
                .map(transferObject -> new Book(transferObject.getName(), now()))
                .toList();
        addBooks(student, transformToEntities.apply(bookTransferObjects));
        studentRepository.save(student);
        return new StudentDto(student);
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "pagedStudentTransferObjects", allEntries = true),
                @CacheEvict(value = "studentTransferObjectById", key = "{#studentTransferObject.getId()}"),
                @CacheEvict(value = "studentTransferObjectsByName", allEntries = true),
                @CacheEvict(value = "studentTransferObjects", allEntries = true),
            })
    public StudentDto addOrUpdateStudent(final StudentDto studentTransferObject) {
        return isNull(studentTransferObject.getId())
                ? addStudent(studentTransferObject)
                : updateStudent(studentTransferObject);
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "pagedStudentTransferObjects", allEntries = true),
                @CacheEvict(value = "studentTransferObjectById", key = "{#studentId}"),
                @CacheEvict(value = "studentTransferObjectsByName", allEntries = true),
                @CacheEvict(value = "studentTransferObjects", allEntries = true),
            })
    public StudentDto enrollToCourse(final Long studentId, final List<CourseDto> courseTransferObjects) {
        logger.info("Enrolling student to course...");
        var student = getStudentById(studentId);
        addEnrollments(student, validateAndFetchCourses(courseTransferObjects));
        return new StudentDto(student);
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
        var studentDto = new StudentDto(getStudentById(studentId));
        studentRepository.deleteById(studentId);
        return studentDto;
    }

    @Cacheable(
            value = "pagedStudentTransferObjects",
            key = "{#page, #itemsPerPage}",
            condition = "@environment.getProperty('app.enable-cache')",
            unless = "#result==null OR #result.isEmpty()")
    public List<StudentDto> getPagedStudentRecords(final int page, final int itemsPerPage) {
        logger.info("Getting student with pagination page {{}}, items {{}}", page, itemsPerPage);
        return studentRepository.findAll(PageRequest.of(page, itemsPerPage)).stream()
                .map(StudentDto::new)
                .toList();
    }

    @Cacheable(
            value = "studentTransferObjectById",
            key = "{#id}",
            condition = "@environment.getProperty('app.enable-cache')",
            unless = "#result==null")
    public StudentDto getStudentDtoById(final Long id) {
        return studentRepository.selectStudentById(id).orElseThrow(() -> {
            var message = "Student with id '%d' is not found".formatted(id);
            return new UniversityException(message, new IllegalArgumentException(message));
        });
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

    private StudentDto addStudent(final StudentDto studentTransferObject) {
        logger.info("Adding student...");
        var courses = validateEnrollments(studentTransferObject.getEnrollments());
        var studentCard = new StudentCard(new Faker().number().digits(cardNumberLength));
        var student = new Student(
                studentTransferObject.getFirstName(),
                studentTransferObject.getLastName(),
                studentTransferObject.getEmail(),
                studentTransferObject.getAge());
        student.setStudentCard(studentCard);
        addBooks(student, studentTransferObject.getBooks());
        var savedStudent = studentRepository.save(student);
        addEnrollments(savedStudent, courses);
        studentTransferObject.setId(savedStudent.getId());
        return studentTransferObject;
    }

    private StudentDto updateStudent(final StudentDto studentTransferObject) {
        logger.info("Updating student...");
        var existingStudent = getStudentById(studentTransferObject.getId());
        setFields(studentTransferObject, existingStudent);
        processUpdatedBooks(studentTransferObject.getBooks(), existingStudent);
        processUpdatedEnrollments(studentTransferObject.getEnrollments(), existingStudent);
        studentRepository.save(existingStudent);
        return studentTransferObject;
    }

    private void processUpdatedEnrollments(final List<EnrollmentDto> enrollments, final Student student) {
        if (isNotEmpty(enrollments)) {
            var existingEnrollments = new ArrayList<>(student.getEnrollments());
            var filteredEnrollments = new ArrayList<>(enrollments);
            existingEnrollments.forEach(existingEnrollment -> {
                var asTransferObject = new EnrollmentDto(existingEnrollment);
                if (!enrollments.contains(asTransferObject)) {
                    student.unenroll(existingEnrollment);
                    return;
                }
                filteredEnrollments.remove(asTransferObject);
            });
            addEnrollments(student, validateEnrollments(filteredEnrollments));
        }
    }

    private void processUpdatedBooks(final List<Book> books, final Student student) {
        if (isNotEmpty(books)) {
            var existingBooks = new ArrayList<>(student.getBooks());
            existingBooks.forEach(student::removeBook);
            addBooks(student, books);
        }
    }

    private void setFields(final StudentDto studentTransferObject, final Student existingStudent) {
        existingStudent.setAge(studentTransferObject.getAge());
        existingStudent.setEmail(studentTransferObject.getEmail());
        existingStudent.setFirstName(studentTransferObject.getFirstName());
        existingStudent.setLastName(studentTransferObject.getLastName());
    }

    private void addEnrollments(final Student student, final List<Course> courses) {
        if (isNotEmpty(courses)) {
            courses.forEach(course -> {
                var enrollmentId = new EnrollmentId(student.getId(), course.getId());
                var enrollment = new Enrollment(course, now(), enrollmentId, student);
                student.enroll(enrollment);
            });
            studentRepository.save(student);
        }
    }

    private List<Course> validateAndFetchCourses(List<CourseDto> courseTransferObjects) {
        return courseTransferObjects.stream()
                .map(course -> courseRepository
                        .selectCourseByNameAndDepartment(course.getName(), course.getDepartment())
                        .orElseThrow(() -> new UniversityException(
                                "Course is invalid '%s'".formatted(course), new IllegalArgumentException())))
                .toList();
    }

    private List<Course> validateEnrollments(final List<EnrollmentDto> enrollments) {
        if (isNotEmpty(enrollments)) {
            return validateAndFetchCourses(
                    enrollments.stream().map(EnrollmentDto::getCourse).toList());
        }
        return emptyList();
    }

    private Student getStudentById(final Long id) {
        return studentRepository
                .findById(id)
                .orElseThrow(() -> new UniversityException(
                        "Student with id '%d' is not found".formatted(id), new IllegalArgumentException()));
    }

    private void addBooks(final Student student, final List<Book> books) {
        if (isNotEmpty(books)) {
            Consumer<Book> setCreatedAtAndAddToStudent = book -> {
                book.setCreatedAt(now());
                student.addBook(book);
            };
            books.forEach(setCreatedAtAndAddToStudent);
        }
    }
}