package com.refresher.security.controller;

import com.refresher.domain.ApplicationResponse;
import com.refresher.security.domain.AuthenticationRequest;
import com.refresher.security.domain.AuthenticationResponse;
import com.refresher.security.domain.UserAccount;
import com.refresher.security.service.AuthenticationService;
import com.refresher.util.ApplicationResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Authentication Controller", description = "Application User Registration/Authentication APIs")
@RestController
@RequestMapping("api/v1/security")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ApplicationResponseMapper applicationResponseMapper;

    @Autowired
    public AuthenticationController(
            final AuthenticationService authenticationService,
            final ApplicationResponseMapper applicationResponseMapper) {
        this.authenticationService = authenticationService;
        this.applicationResponseMapper = applicationResponseMapper;
    }

    @Operation(
            summary = "Register application user",
            description = "Register an application user. Allowable roles are: ADMIN, ADMIN_TRAINEE and STUDENT",
            tags = { "application-user", "security", "post" })
    @ApiResponses(
            value={
                    @ApiResponse(responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = ApplicationResponse.class),
                                    mediaType = "application/json") }),
                    @ApiResponse(responseCode = "400", content = {
                            @Content(schema = @Schema(implementation = ApplicationResponse.class),
                                    mediaType = "application/json") }),
                    @ApiResponse(responseCode = "403", content = {
                            @Content(schema = @Schema(implementation = ApplicationResponse.class),
                                    mediaType = "application/json")})
            })
    @PostMapping("/register")
    public ResponseEntity<ApplicationResponse> register(final @RequestBody UserAccount userAccount) {
        var applicationResponse = applicationResponseMapper.getApplicationResponse(
                ApplicationResponse.ResponseStatus.SUCCESS.name(),
                new AuthenticationResponse(authenticationService.register(userAccount)),
                OK);
        return new ResponseEntity<>(applicationResponse, OK);
    }

    @Operation(
            summary = "Authenticates application user",
            description = "Authenticates an application user. Returns JWT which will be used as mode of authentication.",
            tags = { "application-user", "security", "post" })
    @ApiResponses(
            value={
                    @ApiResponse(responseCode = "200", content = {
                            @Content(schema = @Schema(implementation = ApplicationResponse.class),
                                    mediaType = "application/json") }),
                    @ApiResponse(responseCode = "400", content = {
                            @Content(schema = @Schema(implementation = ApplicationResponse.class),
                                    mediaType = "application/json") }),
                    @ApiResponse(responseCode = "403", content = {
                            @Content(schema = @Schema(implementation = ApplicationResponse.class),
                                    mediaType = "application/json")})
            })
    @PostMapping("/authenticate")
    public ResponseEntity<ApplicationResponse> authenticate(
            final @RequestBody AuthenticationRequest authenticationRequest) {
        var applicationResponse = applicationResponseMapper.getApplicationResponse(
                ApplicationResponse.ResponseStatus.SUCCESS.name(),
                new AuthenticationResponse(authenticationService.authenticate(authenticationRequest)),
                OK);
        return new ResponseEntity<>(applicationResponse, OK);
    }
}
