package com.craftmaster.lds.fgc.answer;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
  List<Score> findTop25ByScoreGreaterThanOrderByScoreDesc(long score);

  default List<Score> top25Scores() {
    return findTop25ByScoreGreaterThanOrderByScoreDesc(0);
  }
}
