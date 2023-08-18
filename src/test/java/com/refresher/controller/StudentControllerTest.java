package com.refresher.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.refresher.domain.ApplicationResponse;
import com.refresher.dto.StudentDto;
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("all")
class StudentControllerTest {

  private static final String SUCCESS = "SUCCESS";
  private static final String ERROR = "ERROR";
  private static final String ACCESS_DENIED = "Access Denied";
  @Autowired private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @WithMockUser(
      value = "admin",
      roles = {"ADMIN"})
  @DisplayName("It should return student records when pagination is enabled.")
  void itShouldReturnPaginatedStudents() throws Exception {
    final int EXPECTED_COUNT = 1;
    final MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/students/paged")
                    .param("page", "0")
                    .param("items", "1")
                    .accept(APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    final ApplicationResponse parsedResponse =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), ApplicationResponse.class);
    final List<StudentDto> students = (List<StudentDto>) parsedResponse.getData();
    assertAll(
        () -> assertEquals(SUCCESS, parsedResponse.getStatus()),
        () -> assertEquals(EXPECTED_COUNT, students.size()));
  }

  @Test
  @WithMockUser(
      value = "student",
      roles = {"STUDENT"})
  @DisplayName("It should return all students from the database.")
  void itShouldReturnStudents() throws Exception {
    final int EXPECTED_COUNT = 3;
    final MvcResult mvcResult =
        mockMvc
            .perform(get("/api/v1/students").accept(APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    final ApplicationResponse applicationResponse =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), ApplicationResponse.class);
    final List<StudentDto> students = (List<StudentDto>) applicationResponse.getData();
    assertAll(
        () -> assertEquals(SUCCESS, applicationResponse.getStatus()),
        () -> assertEquals(EXPECTED_COUNT, students.size()));
  }

  @Test
  @WithMockUser(
      value = "student",
      roles = {"STUDENT"})
  @DisplayName(
      "It should return all students from the database where first/last names matched with parameter.")
  void itShouldReturnStudentWithMatchingName() throws Exception {
    final String NAME_TO_MATCH = "Rosann";
    final int EXPECTED_COUNT = 1;
    final MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/students/name")
                    .param("student-name", NAME_TO_MATCH)
                    .accept(APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    final ApplicationResponse parsedResponse =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), ApplicationResponse.class);
    final List<StudentDto> students = (ArrayList<StudentDto>) parsedResponse.getData();
    assertAll(
        () -> assertEquals(SUCCESS, parsedResponse.getStatus()),
        () -> assertEquals(EXPECTED_COUNT, students.size()),
        () -> assertTrue(result.getResponse().getContentAsString().contains(NAME_TO_MATCH)));
  }

  @Test
  @DisplayName("It should not authorize access to endpoint when no user is specified.")
  void itShouldNotAuthorize() throws Exception {
    final String NAME_TO_MATCH = "Rosann";
    final MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/students/name")
                    .param("student-name", NAME_TO_MATCH)
                    .accept(APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();
    final ApplicationResponse parsedResponse =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), ApplicationResponse.class);
    assertAll(
        () -> assertEquals(ERROR, parsedResponse.getStatus()),
        () -> assertEquals(ACCESS_DENIED, parsedResponse.getMessage()));
  }

  @Test
  @WithMockUser(
      value = "student",
      roles = {"UNKNOWN"})
  @DisplayName("It should not grant access to endpoint when role defined is not valid.")
  void itShouldNotGrantAccess() throws Exception {
    final String NAME_TO_MATCH = "Rosann";
    final MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/students/name")
                    .param("student-name", NAME_TO_MATCH)
                    .accept(APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();
    final ApplicationResponse parsedResponse =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), ApplicationResponse.class);
    assertAll(
        () -> assertEquals(ERROR, parsedResponse.getStatus()),
        () -> assertEquals(ACCESS_DENIED, parsedResponse.getMessage()));
  }
}
