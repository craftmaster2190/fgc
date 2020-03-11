package com.craftmaster.lds.fgc.config;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigPropertyRepository extends JpaRepository<ConfigProperty, String> {
  List<ConfigProperty> findByKeyStartsWith(String prefix);
}
