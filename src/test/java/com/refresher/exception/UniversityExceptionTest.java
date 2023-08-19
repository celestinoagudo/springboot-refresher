package com.refresher.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UniversityExceptionTest {

  @Test
  @DisplayName("It should create UniversityException instance w/ message as constructor argument.")
  void itShouldCreateUniversityException() {
    final var message = "University Exception";
    final var universityException = new UniversityException(message);
    assertAll(
        () -> {
          assertNotNull(universityException);
        },
        () -> {
          assertEquals(message, universityException.getMessage());
        });
  }

  @Test
  @DisplayName(
      "It should create UniversityException instance w/ message & cause as constructor arguments.")
  void itShouldCreateUniversityExceptionWithMessageAndCause() {
    final var message = "University Exception";
    final var cause = new NumberFormatException("Test");
    final var universityException = new UniversityException(message, cause);
    assertAll(
        () -> {
          assertNotNull(universityException);
        },
        () -> {
          assertEquals(message, universityException.getMessage());
        },
        () -> {
          assertTrue(universityException.getCause() instanceof NumberFormatException);
        });
  }
}
