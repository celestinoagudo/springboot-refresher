package com.refresher.controller;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.refresher.dto.BookDto;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("all")
class StudentManagementControllerTest {
  private static final String SUCCESS = "SUCCESS";
  private static final String ERROR = "ERROR";
  private static final String ACCESS_DENIED = "Access Denied";
  @Autowired private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws Exception {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @WithMockUser(authorities = {"student:write"})
  @DisplayName("It should be able to add books to students")
  void itShouldAddBooksToStudent() throws Exception {
    final List<BookDto> payload =
        List.of(
            new BookDto(now(), "First Book"),
            new BookDto(now(), "Second Book"),
            new BookDto(now(), "Third Book"));
    final MvcResult mockMvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/api/management/v1/students/book/{id}", "1")
                    .content(objectMapper.writeValueAsString(payload))
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
  }
}
