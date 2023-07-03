package com.refresher.security.constant;

public enum ApplicationUserAuthority {
  COURSE_READ("course:read"),
  COURSE_WRITE("course:write"),
  STUDENT_READ("student:read"),
  STUDENT_WRITE("student:write");

  private final String authority;

  private ApplicationUserAuthority(final String authority) {
    this.authority = authority;
  }

  public String getAuthority() {
    return authority;
  }
}
