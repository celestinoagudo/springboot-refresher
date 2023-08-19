package com.refresher.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppAuthenticationExceptionTest {

  @Test
  @DisplayName(
      "It should create AppAuthenticationException instance w/ message as constructor argument.")
  void itShouldCreateAppAuthenticationExceptionWithMessage() {
    final var message = "Authentication Exception";
    final var authenticationException = new AppAuthenticationException(message);
    assertAll(
        () -> {
          assertNotNull(authenticationException);
        },
        () -> {
          assertEquals(message, authenticationException.getMessage());
        });
  }

  @Test
  @DisplayName(
      "It should create AppAuthenticationException instance w/ message & cause as constructor arguments.")
  void itShouldCreateAppAuthenticationExceptionWithMessageAndCause() {
    final var message = "Authentication Exception";
    final var cause = new NullPointerException("Test");
    final var authenticationException = new AppAuthenticationException(message, cause);
    assertAll(
        () -> {
          assertNotNull(authenticationException);
        },
        () -> {
          assertEquals(message, authenticationException.getMessage());
        },
        () -> {
          assertTrue(authenticationException.getCause() instanceof NullPointerException);
        });
  }
}
