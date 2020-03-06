package com.craftmaster.lds.fgc.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {

  Optional<Family> findByNameIgnoreCase(String name);

  List<Family> findByNameContainingIgnoreCase(String partialFamilyName);
}
