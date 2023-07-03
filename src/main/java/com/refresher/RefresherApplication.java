package com.refresher;

import com.refresher.repository.CourseRepository;
import com.refresher.repository.StudentRepository;
import com.refresher.security.service.ApplicationUserService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
@SecurityScheme(
    name = "jwt",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT")
public class RefresherApplication {

  public static void main(String[] args) {
    SpringApplication.run(RefresherApplication.class, args);
  }

  @Bean
  @Autowired
  CommandLineRunner commandLineRunner(
      final ApplicationUserService applicationUserService,
      final StudentRepository studentRepository,
      final CourseRepository courseRepository) {
    return args -> {
      //            DataGenerator.generateStudents(studentRepository, courseRepository);
      //            DataGenerator.generateApplicationUsers(applicationUserService);
    };
  }
}
