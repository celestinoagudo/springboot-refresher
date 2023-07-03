package com.refresher.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    private final String applicationTitle;
    private final String version;
    private final String description;

    public OpenApiConfiguration(
            final @Value("${app.title}") String applicationTitle,
            final @Value("${app.version}") String version,
            final @Value("${app.description}") String description) {
        this.applicationTitle = applicationTitle;
        this.version = version;
        this.description = description;
    }

    @Bean
    public OpenAPI apiConfiguration() {
        var apiInformation = new Info().title(applicationTitle).version(version).description(description);
        return new OpenAPI().info(apiInformation);
    }
}
