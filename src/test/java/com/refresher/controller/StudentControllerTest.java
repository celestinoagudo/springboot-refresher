package com.refresher.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.refresher.domain.ApplicationResponse;
import com.refresher.security.domain.AuthenticationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("all")
class StudentControllerTest {
  @Autowired private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  @DisplayName("It should return student records when pagination is enabled.")
  void itShouldReturnPaginatedStudents() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/students/paged")
                .param("page", "0")
                .param("items", "1")
                .accept(APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(roles = {"STUDENT"})
  @DisplayName("It should return all students from the database.")
  void itShouldReturnStudents() throws Exception {
    mockMvc
        .perform(get("/api/v1/students").accept(APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(roles = {"STUDENT"})
  @DisplayName(
      "It should return all students from the database where first/last names matched with parameter.")
  void itShouldReturnStudentWithMatchingName() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/students/name")
                .param("student-name", "Rosann")
                .accept(APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("It should retrieve student by name with authorization header supplied")
  void itShouldAccessEndpointWithAuthorizationHeader() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/students/name")
                .param("student-name", "Rosann")
                .header(HttpHeaders.AUTHORIZATION, getToken("admin", "admin"))
                .accept(APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  private String getToken(final String username, final String password) throws Exception {
    final var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/security/authenticate")
                    .content(
                        objectMapper.writeValueAsString(
                            new AuthenticationRequest(username, password)))
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON))
            .andReturn();
    final var applicationResponse =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), ApplicationResponse.class);
    final var rawToken = applicationResponse.getData().toString().replaceAll("token=", "");
    final var jwt = rawToken.substring(1, rawToken.length() - 1);
    return "Bearer %s".formatted(jwt);
  }

  @Test
  @DisplayName("It should not authorize access to retrieve user by name when no user is specified.")
  void itShouldNotAuthorize() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/students/name")
                .param("student-name", "Rosann")
                .accept(APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(roles = {"UNKNOWN"})
  @DisplayName(
      "It should not grant access to retrieve user by name when role defined is not valid.")
  void itShouldNotGrantAccess() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/students/name")
                .param("student-name", "Rosann")
                .accept(APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }
}
