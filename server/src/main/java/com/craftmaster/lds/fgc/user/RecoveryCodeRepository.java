package com.craftmaster.lds.fgc.user;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, String> {
  void deleteByUserId(UUID userId);
}
