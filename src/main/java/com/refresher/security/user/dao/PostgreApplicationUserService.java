package com.refresher.security.user.dao;

import static java.util.Objects.isNull;

import com.refresher.exception.UniversityException;
import com.refresher.repository.StudentRepository;
import com.refresher.security.domain.ApplicationUser;
import com.refresher.security.domain.UserAccount;
import com.refresher.security.repository.UserAccountRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("postgre")
public class PostgreApplicationUserService implements ApplicationUserDao {

  private final PasswordEncoder passwordEncoder;
  private final StudentRepository studentRepository;
  private final UserAccountRepository userAccountRepository;

  @Autowired
  public PostgreApplicationUserService(
      final PasswordEncoder passwordEncoder,
      final UserAccountRepository userAccountRepository,
      final StudentRepository studentRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userAccountRepository = userAccountRepository;
    this.studentRepository = studentRepository;
  }

  @Transactional
  public ApplicationUser createApplicationUserAccount(final UserAccount userAccount) {
    final var studentId = userAccount.getStudentId();
    if (!isNull(studentId) && studentRepository.findById(studentId).isEmpty()) {
      throw new UniversityException(
          "Student associated to id '%d' is not found".formatted(studentId));
    }
    userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
    final var savedUserAccount = userAccountRepository.save(userAccount);
    return new ApplicationUser(savedUserAccount);
  }

  @Override
  public Optional<ApplicationUser> selectApplicationUserByUsername(final String username) {
    final var applicationUserAccount =
        userAccountRepository.selectApplicationUserByUsername(username);
    return applicationUserAccount.map(ApplicationUser::new);
  }
}
