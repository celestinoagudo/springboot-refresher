package com.refresher.security.constant;

public enum JwtConstant {
  BEARER("Bearer ");

  private final String value;

  private JwtConstant(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
