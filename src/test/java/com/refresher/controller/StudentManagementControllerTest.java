package com.refresher.controller;

import static java.time.LocalDateTime.now;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.refresher.domain.StudentCard;
import com.refresher.dto.BookDto;
import com.refresher.dto.CourseDto;
import com.refresher.dto.StudentDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SuppressWarnings("all")
class StudentManagementControllerTest {
  @Autowired private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private Faker faker;

  @BeforeEach
  void setup() throws Exception {
    faker = new Faker();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @WithMockUser(authorities = {"student:write"})
  @DisplayName("It should be able to add books to students")
  void itShouldAddBooksToStudent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/management/v1/students/book/{id}", "3")
                .content(objectMapper.writeValueAsString(getBooksPayload()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"student:write"})
  @DisplayName("It should not be able to add books to students when student id is not valid")
  void itShouldNotAddBooksToStudent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/management/v1/students/book/{id}", "9")
                .content(objectMapper.writeValueAsString(getBooksPayload()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  private List<BookDto> getBooksPayload() {
    return List.of(
        new BookDto(now(), faker.book().title()),
        new BookDto(now(), faker.book().title()),
        new BookDto(now(), faker.book().title()));
  }

  @Test
  @DisplayName("It should add/update student")
  @WithMockUser(authorities = {"student:write"})
  void itShouldAddOrUpdateStudent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/management/v1/students")
                .content(objectMapper.writeValueAsString(getStudentPayload()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  private StudentDto getStudentPayload() {
    final var payload = new StudentDto();
    payload.setFirstName(faker.name().firstName());
    payload.setLastName(faker.name().lastName());
    payload.setEmail(
        "%s%s@gmail.com"
            .formatted(payload.getFirstName().toLowerCase(), payload.getLastName().toLowerCase()));
    payload.setAge(faker.number().numberBetween(18, 60));
    final var studentCard = new StudentCard();
    studentCard.setCardNumber(faker.number().digits(15));
    payload.setStudentCard(studentCard);
    return payload;
  }

  @Test
  @DisplayName("It should enroll student to course")
  @WithMockUser(authorities = {"course:write"})
  void itShouldEnrollStudentToCourse() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/management/v1/students/enroll/{id}", "1")
                .content(objectMapper.writeValueAsString(getCoursesPayload(true)))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("It should not enroll student to course when it's not recognized")
  @WithMockUser(authorities = {"course:write"})
  void itShouldNotEnrollStudentToCourse() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/management/v1/students/enroll/{id}", "1")
                .content(objectMapper.writeValueAsString(getCoursesPayload(false)))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  private List<CourseDto> getCoursesPayload(final boolean isValid) {
    final var firstCourse = new CourseDto();
    firstCourse.setDepartment(isValid ? "Many Waters" : "Not Valid");
    firstCourse.setName(isValid ? "Associate Degree in Commerce" : "Not Valid");
    final var secondCourse = new CourseDto();
    secondCourse.setDepartment(isValid ? "Computers and Shoes" : "Not Valid");
    secondCourse.setName(isValid ? "Master of Communications" : "Not Valid");
    return List.of(firstCourse, secondCourse);
  }

  @Test
  @DisplayName("It should retrieve student by id")
  @WithMockUser(roles = {"ADMIN"})
  void itShouldRetrieveStudentById() throws Exception {
    mockMvc
        .perform(get("/api/management/v1/students/{id}", "2"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("It should fail when student is not found")
  @WithMockUser(roles = {"ADMIN"})
  void itShouldNotRetrieveStudentById() throws Exception {
    mockMvc
        .perform(get("/api/management/v1/students/{id}", "9"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @DisplayName("It should delete student by id")
  @WithMockUser(authorities = {"student:write"})
  void itShouldDeleteStudent() throws Exception {
    mockMvc
        .perform(delete("/api/management/v1/students/{studentId}", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("It should not authorize access to delete endpoint when no user is specified.")
  void itShouldNotAuthorize() throws Exception {
    mockMvc
        .perform(delete("/api/management/v1/students/{studentId}", "1"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(authorities = {"student:read"})
  @DisplayName("It should not grant access to delete student when role defined is not valid.")
  void itShouldNotGrantAccess() throws Exception {
    mockMvc
        .perform(delete("/api/management/v1/students/{studentId}", "1"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }
}
