package com.refresher.security.repository;

import com.refresher.security.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query("SELECT userAccount FROM UserAccount userAccount WHERE userAccount.username=?1")
    Optional<UserAccount> selectApplicationUserByUsername(final String username);
}
