package com.refresher.security.constant;

import static com.google.common.collect.Sets.newHashSet;
import static com.refresher.security.constant.ApplicationUserAuthority.*;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum ApplicationUserRole {
  ADMIN(newHashSet(COURSE_READ, COURSE_WRITE, STUDENT_READ, STUDENT_WRITE)),
  ADMIN_TRAINEE(newHashSet(COURSE_READ, STUDENT_READ)),
  STUDENT(newHashSet());

  private final Set<ApplicationUserAuthority> authorities;

  private ApplicationUserRole(final Set<ApplicationUserAuthority> authorities) {
    this.authorities = authorities;
  }

  private Set<ApplicationUserAuthority> getAuthorities() {
    return authorities;
  }

  public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
    final Set<SimpleGrantedAuthority> grantedAuthorities =
        getAuthorities().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getAuthority()))
            .collect(toSet());
    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_%s".formatted(name())));
    return grantedAuthorities;
  }
}
