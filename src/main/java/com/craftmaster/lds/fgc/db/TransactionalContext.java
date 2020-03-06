package com.craftmaster.lds.fgc.db;

import java.util.function.Supplier;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

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
