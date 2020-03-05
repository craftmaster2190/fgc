package com.craftmaster.lds.fgc.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {

  Optional<Family> findByNameIgnoreCase(String name);

  List<Family> findByNameContainingIgnoreCase(String partialFamilyName);
}
