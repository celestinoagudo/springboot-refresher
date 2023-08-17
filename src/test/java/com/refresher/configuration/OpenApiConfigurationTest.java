package com.refresher.configuration;

import static org.junit.jupiter.api.Assertions.*;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class OpenApiConfigurationTest {

  private OpenApiConfiguration unitUnderTest;
  private String applicationTitle;
  private String version;
  private String description;

  @BeforeEach
  public void setup() {
    applicationTitle = "Continental University";
    version = "1.0";
    description = "Continental University Description";
    unitUnderTest = new OpenApiConfiguration(applicationTitle, version, description);
  }

  @Test
  void shouldReturnApiConfiguration() {
    final OpenAPI openAPI = unitUnderTest.apiConfiguration();
    assertAll(
        () -> {
          assertNotNull(openAPI);
        },
        () -> {
          assertNotNull(openAPI.getInfo());
        },
        () -> {
          assertEquals(applicationTitle, openAPI.getInfo().getTitle());
        },
        () -> {
          assertEquals(version, openAPI.getInfo().getVersion());
        },
        () -> {
          assertEquals(description, openAPI.getInfo().getDescription());
        });
  }
}
