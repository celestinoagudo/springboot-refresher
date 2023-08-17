package com.refresher.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.refresher.dto.BookDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SuppressWarnings("all")
class StudentManagementControllerTest {
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

  @Disabled
  void itShouldAddBooksToStudent() throws Exception {
    final BookDto book = new BookDto(LocalDateTime.now(), "Test Book");
  }
}
