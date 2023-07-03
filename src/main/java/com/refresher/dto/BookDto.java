package com.refresher.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(NON_NULL)
public class BookDto {

  private LocalDateTime createdAt;
  private String name;

  public BookDto() {}

  public BookDto(final LocalDateTime createdAt, final String name) {
    this.createdAt = createdAt;
    this.name = name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
