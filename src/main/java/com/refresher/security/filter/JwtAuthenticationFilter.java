package com.refresher.security.filter;

import com.refresher.security.service.JsonWebTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.refresher.security.constant.JwtConstant.BEARER;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JsonWebTokenService jsonWebTokenService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(
            final JsonWebTokenService jsonWebTokenService, final UserDetailsService userDetailsService) {
        this.jsonWebTokenService = jsonWebTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final var authenticationHeader = request.getHeader(AUTHORIZATION);
        final var tokenPrefix = BEARER.getValue();
        if (isNull(authenticationHeader) || !authenticationHeader.startsWith(tokenPrefix)) {
            filterChain.doFilter(request, response);
            return;
        }
        final var jsonWebToken = authenticationHeader.replaceAll(tokenPrefix, "");
        final var username = jsonWebTokenService.extractUsername(jsonWebToken);
        if (!isNull(username) && isNull(getContext().getAuthentication())) {
            var userDetails = userDetailsService.loadUserByUsername(username);
            if (jsonWebTokenService.isTokenValid(jsonWebToken, userDetails)) {
                var authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
