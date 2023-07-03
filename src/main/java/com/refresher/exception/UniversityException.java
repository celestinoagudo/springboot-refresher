package com.refresher.exception;

import java.io.Serial;

public class UniversityException extends RuntimeException {

  @Serial private static final long serialVersionUID = 6602071187048066719L;

  public UniversityException(final String message) {
    super(message);
  }

  public UniversityException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
