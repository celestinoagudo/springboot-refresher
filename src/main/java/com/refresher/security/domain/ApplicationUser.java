package com.refresher.security.domain;

import com.google.common.collect.Sets;
import com.refresher.security.constant.ApplicationUserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class ApplicationUser implements UserDetails {

    private Set<SimpleGrantedAuthority> grantedAuthorities;
    private UserAccount userAccount;

    public ApplicationUser() {}

    public ApplicationUser(final UserAccount userAccount) {
        this.userAccount = userAccount;
        this.grantedAuthorities =
                ApplicationUserRole.valueOf(userAccount.getRole()).getGrantedAuthorities();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ApplicationUser other = (ApplicationUser) object;
        return Objects.equals(
                        getUserAccount().getPassword(), other.getUserAccount().getPassword())
                && Objects.equals(
                        getUserAccount().getUsername(), other.getUserAccount().getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserAccount().getUsername(), getUserAccount().getPassword());
    }

    @Override
    public String toString() {
        return "ApplicationUser{grantedAuthorities=%s, userAccount=%s}".formatted(grantedAuthorities, userAccount);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Sets.newHashSet(grantedAuthorities);
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userAccount.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userAccount.isActive();
    }

    @Override
    public boolean isEnabled() {
        return userAccount.isActive();
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }
}
