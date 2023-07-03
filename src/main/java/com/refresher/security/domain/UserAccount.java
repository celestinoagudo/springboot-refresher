package com.refresher.security.domain;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity(name = "UserAccount")
@Table(
        name = "user_account",
        uniqueConstraints = {@UniqueConstraint(name = "username_unique", columnNames = "username")})
public class UserAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = -8190790655897558052L;

    @Id
    @SequenceGenerator(name = "user_account_sequence", sequenceName = "user_account_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "user_account_sequence")
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "is_active", columnDefinition = "BOOLEAN default false", nullable = false)
    private boolean isActive;

    @Column(name = "password", columnDefinition = "TEXT", nullable = false)
    private String password;

    @Column(name = "role", columnDefinition = "TEXT", nullable = false)
    private String role;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "username", columnDefinition = "TEXT", nullable = false)
    private String username;

    public UserAccount() {}

    public UserAccount(
            final Long id,
            final boolean isActive,
            final String password,
            final String role,
            final Long studentId,
            final String username) {
        this.id = id;
        this.isActive = isActive;
        this.password = password;
        this.role = role;
        this.studentId = studentId;
        this.username = username;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserAccount that = (UserAccount) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "UserAccount{id=%s, isActive=%s, password=%s, role=%s, studentId=%s, username=%s}"
                .formatted(id, isActive, password, role, studentId, username);
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(final boolean active) {
        isActive = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(final Long studentId) {
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }
}
