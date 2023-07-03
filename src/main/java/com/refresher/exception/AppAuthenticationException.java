package com.refresher.exception;

import java.io.Serial;

public class AppAuthenticationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1669189859068785279L;

    public AppAuthenticationException(final String message) {
        super(message);
    }

    public AppAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
