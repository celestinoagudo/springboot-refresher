package com.refresher.controller;

import static org.springframework.http.HttpStatus.OK;

import com.refresher.domain.ApplicationResponse;
import com.refresher.dto.BookDto;
import com.refresher.dto.CourseDto;
import com.refresher.dto.StudentDto;
import com.refresher.services.StudentService;
import com.refresher.util.ApplicationResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "jwt")
@Tag(name = "Student Management Controller", description = "Student Maintenance APIs")
@RestController
@RequestMapping("api/management/v1/students")
public class StudentManagementController {

  private final ApplicationResponseMapper applicationResponseMapper;
  private final StudentService studentService;

  @Autowired
  public StudentManagementController(
      final StudentService studentService,
      final ApplicationResponseMapper applicationResponseMapper) {
    this.studentService = studentService;
    this.applicationResponseMapper = applicationResponseMapper;
  }

  @Operation(
      summary = "Add books to student",
      description =
          "Add books associated to the student id parameter passed. User should have student:write"
              + " authority to perform this operation",
      tags = {"students", "put"})
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
  @PutMapping(path = "/book/{id}")
  @PreAuthorize("hasAuthority(@environment.getProperty('app.authority.student-write'))")
  public ResponseEntity<ApplicationResponse> addBooks(
      @RequestBody final List<BookDto> books, @PathVariable("id") final Long studentId) {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.addBooksTo(studentId, books), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }

  @Operation(
      summary = "Create student",
      description =
          "Add student record to the database. User should have student:write authority to perform "
              + "this operation",
      tags = {"students", "post"})
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
  @PostMapping
  @PreAuthorize("hasAuthority(@environment.getProperty('app.authority.student-write'))")
  public ResponseEntity<ApplicationResponse> addOrUpdateStudent(
      @RequestBody final StudentDto student) {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.addOrUpdateStudent(student), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }

  @Operation(
      summary = "Enroll student to a course",
      description =
          "Enroll student to a course. The course used should be recognized and the user should have"
              + " student:Write authority to perform this operation",
      tags = {"students", "put"})
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
  @PutMapping(path = "/enroll/{id}")
  @PreAuthorize("hasAuthority(@environment.getProperty('app.authority.course-write'))")
  public ResponseEntity<ApplicationResponse> enrollToCourses(
      @RequestBody final List<CourseDto> courses, @PathVariable("id") final Long studentId) {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.enrollToCourse(studentId, courses), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }

  @Operation(
      summary = "Retrieve student by id",
      description =
          "Gets student by the id parameter passed. User should have admin/admin_trainee role to"
              + " perform this operation",
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
  @GetMapping(path = "/{id}")
  @PreAuthorize(
      "hasAnyRole(@environment.getProperty('app.role.admin'), "
          + "@environment.getProperty('app.role.admin-trainee'))")
  public ResponseEntity<ApplicationResponse> getStudentById(@PathVariable("id") final Long id) {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.getStudentDtoById(id), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }

  @Operation(
      summary = "Delete student",
      description =
          "Delete the student associated to the id parameter passed. User should have student:write "
              + "authority to perform this operation",
      tags = {"students", "delete"})
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
  @DeleteMapping(path = "/{studentId}")
  @PreAuthorize("hasAuthority(@environment.getProperty('app.authority.student-write'))")
  public ResponseEntity<ApplicationResponse> removeStudent(
      @PathVariable("studentId") final Long studentId) {
    var applicationResponse =
        applicationResponseMapper.getApplicationResponse(
            OK.getReasonPhrase(), studentService.removeStudent(studentId), OK);
    return new ResponseEntity<>(applicationResponse, OK);
  }
}
