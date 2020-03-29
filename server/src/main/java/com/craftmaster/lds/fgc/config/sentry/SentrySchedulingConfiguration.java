package com.craftmaster.lds.fgc.config.sentry;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Slf4j
@Configuration
@Conditional(SentryCondition.Enabled.class)
public class SentrySchedulingConfiguration implements SchedulingConfigurer {
  private final ThreadPoolTaskScheduler taskScheduler;

  public SentrySchedulingConfiguration() {
    log.info("Loading {}", this.getClass().getSimpleName());
    taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setErrorHandler(
        throwable -> {
          log.error("Exception in @Scheduled task. ", throwable);
          Sentry.getContext().addExtra("Caught in", this.getClass().getName());
          Sentry.capture(throwable);
        });
    taskScheduler.setThreadNamePrefix("@scheduled-");

    taskScheduler.initialize();
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskScheduler);
  }
}
