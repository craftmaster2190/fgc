package com.craftmaster.lds.fgc.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends CrudRepository<Family, UUID> {

  Optional<Family> findByName(String name);

  List<Family> findByNameContainingIgnoreCase(String partialFamilyName);
}
