package com.refresher;

import com.github.javafaker.Faker;
import com.refresher.domain.*;
import com.refresher.repository.CourseRepository;
import com.refresher.repository.StudentRepository;
import com.refresher.security.constant.ApplicationUserRole;
import com.refresher.security.domain.UserAccount;
import com.refresher.security.service.ApplicationUserService;
import java.time.LocalDateTime;

public class DataGenerator {

  private DataGenerator() {}

  public static void generateApplicationUsers(final ApplicationUserService applicationUserService) {
    final var adminUserAccount = new UserAccount();
    adminUserAccount.setUsername("admin");
    adminUserAccount.setPassword("admin");
    adminUserAccount.setActive(true);
    adminUserAccount.setRole(ApplicationUserRole.ADMIN.name());

    final var adminTraineeUserAccount = new UserAccount();
    adminTraineeUserAccount.setUsername("admin_trainee");
    adminTraineeUserAccount.setPassword("admin_trainee");
    adminTraineeUserAccount.setActive(true);
    adminTraineeUserAccount.setRole(ApplicationUserRole.ADMIN_TRAINEE.name());

    final var studentUserAccount = new UserAccount();
    studentUserAccount.setUsername("student");
    studentUserAccount.setPassword("student");
    studentUserAccount.setStudentId(1L);
    studentUserAccount.setActive(true);
    studentUserAccount.setRole(ApplicationUserRole.STUDENT.name());

    applicationUserService.createApplicationUserAccount(adminUserAccount);
    applicationUserService.createApplicationUserAccount(adminTraineeUserAccount);
    applicationUserService.createApplicationUserAccount(studentUserAccount);
  }

  public static void generateStudents(
      final StudentRepository studentRepository, final CourseRepository courseRepository) {
    final Faker faker = new Faker();
    for (int counter = 0; counter <= 49; ++counter) {
      final var firstName = faker.name().firstName();
      final var lastName = faker.name().lastName();
      final var email = "%s%s@continental.edu".formatted(firstName, lastName).toLowerCase();
      final var student = new Student();
      student.setFirstName(firstName);
      student.setLastName(lastName);
      student.setEmail(email);
      student.setAge(faker.number().numberBetween(18, 60));

      final var studentCard = new StudentCard();
      studentCard.setCardNumber(faker.number().digits(15));

      final var firstCourse = new Course();
      firstCourse.setDepartment(faker.commerce().department());
      firstCourse.setName(faker.educator().course());
      final var secondCourse = new Course();
      secondCourse.setDepartment(faker.commerce().department());
      secondCourse.setName(faker.educator().course());
      final var thirdCourse = new Course();
      thirdCourse.setDepartment(faker.commerce().department());
      thirdCourse.setName(faker.educator().course());

      final var firstBook = new Book();
      firstBook.setName(faker.book().title());
      firstBook.setCreatedAt(LocalDateTime.now());
      final var secondBook = new Book();
      secondBook.setName(faker.book().title());
      secondBook.setCreatedAt(LocalDateTime.now());
      final var thirdBook = new Book();
      thirdBook.setName(faker.book().title());
      thirdBook.setCreatedAt(LocalDateTime.now());

      student.setStudentCard(studentCard);
      student.addBook(firstBook);
      student.addBook(secondBook);
      student.addBook(thirdBook);

      final Student savedStudent = studentRepository.save(student);
      final Course savedFirstCourse = courseRepository.save(firstCourse);
      final Course savedSecondCourse = courseRepository.save(secondCourse);
      final Course savedThirdCourse = courseRepository.save(thirdCourse);

      final var firstEnrollmentId =
          new EnrollmentId(savedFirstCourse.getId(), savedStudent.getId());
      final var secondEnrollmentId =
          new EnrollmentId(savedSecondCourse.getId(), savedStudent.getId());
      final var thirdEnrollmentId =
          new EnrollmentId(savedThirdCourse.getId(), savedStudent.getId());

      final var firstEnrollment =
          new Enrollment(firstCourse, LocalDateTime.now(), firstEnrollmentId, student);
      final var secondEnrollment =
          new Enrollment(secondCourse, LocalDateTime.now(), secondEnrollmentId, student);
      final var thirdEnrollment =
          new Enrollment(thirdCourse, LocalDateTime.now(), thirdEnrollmentId, student);

      savedStudent.enroll(firstEnrollment);
      savedStudent.enroll(secondEnrollment);
      savedStudent.enroll(thirdEnrollment);
      studentRepository.save(savedStudent);
    }
  }
}
