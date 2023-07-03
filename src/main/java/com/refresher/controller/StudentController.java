package com.refresher.controller;

import static org.springframework.http.HttpStatus.OK;

import com.refresher.domain.ApplicationResponse;
import com.refresher.services.StudentService;
import com.refresher.util.ApplicationResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "jwt")
@Tag(name = "Student Controller", description = "Student Retrieving APIs")
@RestController
@RequestMapping("api/v1/students")
public class StudentController {

  private final ApplicationResponseMapper applicationResponseMapper;
  private final StudentService studentService;

  @Autowired
  public StudentController(
      final StudentService studentService,
      final ApplicationResponseMapper applicationResponseMapper) {
    this.studentService = studentService;
    this.applicationResponseMapper = applicationResponseMapper;
  }

  @Operation(
      summary = "Retrieve students w/ pagination",
      description =
          "Gets students where pagination is implemented through the use of page-number and number of"
              + " students per page. User should have appropriate role(s) when accessing this resource",
      tags = {"students", "get"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            }),
        @ApiResponse(
            responseCode = "400",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            }),
        @ApiResponse(
            responseCode = "403",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            })
      })
  @GetMapping(path = "/paged")
  @PreAuthorize(
      "hasAnyRole(@environment.getProperty('app.role.student'), "
          + "@environment.getProperty('app.role.admin'), "
          + "@environment.getProperty('app.role.admin-trainee'))")
  public ResponseEntity<ApplicationResponse> getPaged(
      @RequestParam("page") final int page, @RequestParam("items") final int itemsPerPage) {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.getPagedStudentRecords(page, itemsPerPage), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }

  @Operation(
      summary = "Retrieve students",
      description =
          "Retrieve all students. User should have appropriate role(s) when accessing this resource",
      tags = {"students", "get"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            }),
        @ApiResponse(
            responseCode = "400",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            }),
        @ApiResponse(
            responseCode = "403",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            })
      })
  @GetMapping
  @PreAuthorize(
      "hasAnyRole(@environment.getProperty('app.role.student'), "
          + "@environment.getProperty('app.role.admin'), "
          + "@environment.getProperty('app.role.admin-trainee'))")
  public ResponseEntity<ApplicationResponse> getStudents() {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.getStudents(), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }

  @Operation(
      summary = "Retrieve students by name",
      description =
          "Retrieve all students where the first name or last name matches the parameter. User should "
              + "have appropriate role(s) when accessing this resource",
      tags = {"students", "get"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            }),
        @ApiResponse(
            responseCode = "400",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            }),
        @ApiResponse(
            responseCode = "403",
            content = {
              @Content(
                  schema = @Schema(implementation = ApplicationResponse.class),
                  mediaType = "application/json")
            })
      })
  @GetMapping("/name")
  @PreAuthorize(
      "hasAnyRole(@environment.getProperty('app.role.student'), "
          + "@environment.getProperty('app.role.admin'), "
          + "@environment.getProperty('app.role.admin-trainee'))")
  public ResponseEntity<ApplicationResponse> getStudentsByName(
      @RequestParam("student-name") final String studentName) {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.getStudentDtoByName(studentName), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }
}
