package com.craftmaster.lds.fgc.answer;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class Scores {

  private Map<String, Long> user2Score;
  private Map<String, Long> family2Score;
}
