package com.refresher.security.service;

import com.refresher.exception.UniversityException;
import com.refresher.security.constant.ApplicationUserRole;
import com.refresher.security.domain.AuthenticationRequest;
import com.refresher.security.domain.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.EnumUtils.isValidEnum;

@Service
public class AuthenticationService {

    private final ApplicationUserService applicationUserService;
    private final JsonWebTokenService jsonWebTokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(
            final ApplicationUserService applicationUserService,
            final JsonWebTokenService jsonWebTokenService,
            final AuthenticationManager authenticationManager) {
        this.applicationUserService = applicationUserService;
        this.jsonWebTokenService = jsonWebTokenService;
        this.authenticationManager = authenticationManager;
    }

    public String register(final UserAccount userAccount) {
        if (!isValidEnum(ApplicationUserRole.class, userAccount.getRole()))
            throw new UniversityException("Invalid role '%s'".formatted(userAccount.getRole()));
        final var applicationUserAccount = applicationUserService.createApplicationUserAccount(userAccount);
        return jsonWebTokenService.generateToken(applicationUserAccount);
    }

    public String authenticate(final AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        final var applicationUserAccount =
                applicationUserService.loadUserByUsername(authenticationRequest.getUsername());
        return jsonWebTokenService.generateToken(applicationUserAccount);
    }
}
