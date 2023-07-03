package com.refresher.security.user.dao;

import com.refresher.security.domain.ApplicationUser;
import com.refresher.security.domain.UserAccount;
import java.util.Optional;

public interface ApplicationUserDao {
  Optional<ApplicationUser> selectApplicationUserByUsername(final String username);

  ApplicationUser createApplicationUserAccount(final UserAccount userAccount);
}
