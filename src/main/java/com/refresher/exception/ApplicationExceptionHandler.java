package com.refresher.exception;

import com.refresher.domain.ApplicationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.StringUtils.hasText;

@RestController
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private final String applicationName;
    private final String defaultErrorMessage;
    private final String version;

    public ApplicationExceptionHandler(
            final @Value("${app.title}") String applicationName,
            final @Value("${app.version}") String version,
            final @Value("${app.default-error-message}") String defaultErrorMessage) {
        this.applicationName = applicationName;
        this.version = version;
        this.defaultErrorMessage = defaultErrorMessage;
    }

    @ExceptionHandler
    public final ResponseEntity<ApplicationResponse> handleAuthenticationException(
            final AuthenticationException authException, final WebRequest request) {
        final ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setProgramName(applicationName);
        applicationResponse.setVersion(version);
        applicationResponse.setDatetime(now());
        applicationResponse.setStatus(ApplicationResponse.ResponseStatus.ERROR.name());
        applicationResponse.setCode(FORBIDDEN.value());
        applicationResponse.setMessage(authException.getMessage());
        return new ResponseEntity<>(applicationResponse, FORBIDDEN);
    }

    @ExceptionHandler
    public final ResponseEntity<ApplicationResponse> handleGenericException(
            final Exception genericException, final WebRequest request) {
        final ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setProgramName(applicationName);
        applicationResponse.setVersion(version);
        applicationResponse.setDatetime(now());
        applicationResponse.setStatus(ApplicationResponse.ResponseStatus.ERROR.name());
        applicationResponse.setCode(BAD_REQUEST.value());
        applicationResponse.setMessage(
                hasText(genericException.getMessage())
                        ? genericException.getMessage()
                        : defaultErrorMessage);
        return new ResponseEntity<>(applicationResponse, FORBIDDEN);
    }

    @ExceptionHandler
    public final ResponseEntity<ApplicationResponse> handleUniversityException(
            final UniversityException universityException, final WebRequest request) {
        final ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setProgramName(applicationName);
        applicationResponse.setVersion(version);
        applicationResponse.setDatetime(now());
        applicationResponse.setStatus(ApplicationResponse.ResponseStatus.ERROR.name());
        applicationResponse.setCode(BAD_REQUEST.value());
        applicationResponse.setMessage(universityException.getMessage());
        return new ResponseEntity<>(applicationResponse, BAD_REQUEST);
    }
}
