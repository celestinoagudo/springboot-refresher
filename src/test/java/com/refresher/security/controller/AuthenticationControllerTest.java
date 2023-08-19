package com.refresher.security.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.refresher.security.constant.ApplicationUserRole;
import com.refresher.security.domain.AuthenticationRequest;
import com.refresher.security.domain.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SuppressWarnings("all")
class AuthenticationControllerTest {

  @Autowired private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws Exception {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @DisplayName("It should register new application user")
  void itShouldRegisterApplicationUser() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/security/register")
                .content(
                    objectMapper.writeValueAsString(
                        getApplicationUserPayload(ApplicationUserRole.ADMIN_TRAINEE.name(), 2L)))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("It should not register new application user when role is invalid")
  void itShouldNotRegisterApplicationUser() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/security/register")
                .content(
                    objectMapper.writeValueAsString(getApplicationUserPayload("INVALID_ROLE", 2L)))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @DisplayName("It should not register new application user when student id is invalid")
  void itShouldNotRegisterApplicationUserWhenStudentIsNonExistent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/security/register")
                .content(
                    objectMapper.writeValueAsString(
                        getApplicationUserPayload(ApplicationUserRole.ADMIN_TRAINEE.name(), 9L)))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @DisplayName("It should successfully authenticate application user")
  void itShouldSuccessfullyAuthenticate() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/security/authenticate")
                .content(
                    objectMapper.writeValueAsString(
                        getAuthenticationRequestPayload("admin_trainee", "admin_trainee")))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @DisplayName("It should not successfully authenticate an unknown application user")
  void itShouldNotSuccessfullyAuthenticate() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/security/authenticate")
                .content(
                    objectMapper.writeValueAsString(
                        getAuthenticationRequestPayload("unknown_user", "unknown_password")))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  private UserAccount getApplicationUserPayload(final String role, final long studentId) {
    final var newUserAccount = new UserAccount();
    newUserAccount.setUsername("test_application_user");
    newUserAccount.setPassword("test_application_user");
    newUserAccount.setRole(role);
    newUserAccount.setActive(true);
    newUserAccount.setStudentId(studentId);
    return newUserAccount;
  }

  private AuthenticationRequest getAuthenticationRequestPayload(
      final String username, final String password) {
    final var authenticationRequest = new AuthenticationRequest();
    authenticationRequest.setUsername(username);
    authenticationRequest.setPassword(password);
    return authenticationRequest;
  }
}
