package com.craftmaster.lds.fgc.answer;

import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Scores {

  private Map<String, Long> user2Score;
  private Map<String, Long> family2Score;
}
