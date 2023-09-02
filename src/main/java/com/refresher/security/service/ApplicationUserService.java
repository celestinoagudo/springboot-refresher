package com.refresher.security.service;

import com.refresher.security.domain.ApplicationUser;
import com.refresher.security.domain.UserAccount;
import com.refresher.security.user.dao.ApplicationUserDao;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService implements UserDetailsService {

  private final ApplicationUserDao applicationUserDao;

  @Autowired
  public ApplicationUserService(final @Qualifier("postgre") ApplicationUserDao applicationUserDao) {
    this.applicationUserDao = applicationUserDao;
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    final Supplier<UsernameNotFoundException> exceptionForUnrecognizedUsername =
        () -> new UsernameNotFoundException("User '%s' is not found".formatted(username));
    return applicationUserDao
        .selectApplicationUserByUsername(username)
        .orElseThrow(exceptionForUnrecognizedUsername);
  }

  public ApplicationUser createApplicationUserAccount(final UserAccount userAccount) {
    return applicationUserDao.createApplicationUserAccount(userAccount);
  }
}
