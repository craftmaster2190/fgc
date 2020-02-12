package com.craftmaster.lds.fgc.chat;

import com.craftmaster.lds.fgc.user.User;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class Chat {

  private User user;
  private String value;
  private Instant time;
}
