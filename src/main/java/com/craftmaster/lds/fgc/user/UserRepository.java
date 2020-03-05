package com.craftmaster.lds.fgc.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByNameIgnoreCase(String adminUsername);

  Stream<User> findByIsAdminIsNullOrIsAdminIsFalse();
}
