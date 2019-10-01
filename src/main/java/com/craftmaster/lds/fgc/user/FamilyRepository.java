package com.craftmaster.lds.fgc.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends CrudRepository<Family, Long> {

  Optional<Family> findByName(String name);

  List<Family> findByNameContainingIgnoreCase(String partialFamilyName);
}
