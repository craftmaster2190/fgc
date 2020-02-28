package com.craftmaster.lds.fgc.config;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Supplier;

@Service
public class TransactionalContext {
  @Transactional
  public void run(Runnable runnable) {
    runnable.run();
  }

  @Transactional
  public <T> T run(Supplier<T> supplier) {
    return supplier.get();
  }
}
