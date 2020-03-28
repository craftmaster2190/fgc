package com.craftmaster.lds.fgc.user;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByNameIgnoreCase(String adminUsername);

  Stream<User> findByIsAdminIsNullOrIsAdminIsFalse();

  Optional<User> findByNameIgnoreCaseAndFamilyNameIgnoreCase(String userName, String familyName);
}
