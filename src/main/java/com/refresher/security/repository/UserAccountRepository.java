package com.refresher.security.repository;

import com.refresher.security.domain.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

  @Query("SELECT userAccount FROM UserAccount userAccount WHERE userAccount.username=?1")
  Optional<UserAccount> selectApplicationUserByUsername(final String username);
}
