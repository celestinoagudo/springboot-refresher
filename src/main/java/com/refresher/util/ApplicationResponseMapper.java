package com.refresher.util;

import com.refresher.domain.ApplicationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.refresher.domain.ApplicationResponse.ResponseStatus.SUCCESS;

@Component
public class ApplicationResponseMapper {

    private final String applicationName;
    private final String version;

    public ApplicationResponseMapper(
            final @Value("${app.title}") String applicationName, final @Value("${app.version}") String version) {
        this.applicationName = applicationName;
        this.version = version;
    }

    public ApplicationResponse getApplicationResponse(
            final String message, final Object data, final HttpStatus status) {
        var applicationResponse = new ApplicationResponse();
        applicationResponse.setProgramName(applicationName);
        applicationResponse.setVersion(version);
        applicationResponse.setDatetime(LocalDateTime.now());
        applicationResponse.setStatus(SUCCESS.name());
        applicationResponse.setCode(status.value());
        applicationResponse.setMessage(message);
        applicationResponse.setData(data);
        return applicationResponse;
    }
}
