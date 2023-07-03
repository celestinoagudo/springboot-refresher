package com.refresher.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@JsonPropertyOrder({"programName", "version", "datetime", "status", "code", "message", "data"})
public class ApplicationResponse {

    private int code;
    private Object data;
    private LocalDateTime datetime;
    private String message;
    private String programName;
    private String status;
    private String version;

    public ApplicationResponse() {}

    public ApplicationResponse(
            final int code,
            final Object data,
            final LocalDateTime datetime,
            final String message,
            final String programName,
            final String status,
            final String version) {
        this.code = code;
        this.data = data;
        this.datetime = datetime;
        this.message = message;
        this.programName = programName;
        this.status = status;
        this.version = version;
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(final LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(final String programName) {
        this.programName = programName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public enum ResponseStatus {
        ACCEPTED,
        ERROR,
        FAIL,
        SUCCESS
    }
}
