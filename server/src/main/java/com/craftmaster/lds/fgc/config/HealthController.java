package com.craftmaster.lds.fgc.config;

import static java.time.Instant.now;

import java.time.Duration;
import java.time.Instant;
import java.util.function.DoubleSupplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.logging.formatter.DurationFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("health")
public class HealthController {

  @Getter private final Instant startupTime = now();

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public String ping() {
    final Memory totalMemory = new Memory(Runtime.getRuntime().totalMemory());
    final Memory freeMemory = new Memory(Runtime.getRuntime().freeMemory());
    final Memory usedMemory = new Memory(totalMemory.getBytes() - freeMemory.getBytes());

    final double usedMemoryPercent = ((usedMemory.getBytes() * 1.0) / totalMemory.getBytes()) * 100;
    return String.format(
        "Free: %s Used: %s Total: %s (%.2f %%) Up since: %s Uptime: %s",
        freeMemory.humanize(),
        usedMemory.humanize(),
        totalMemory.humanize(),
        usedMemoryPercent,
        getStartupTime(),
        new DurationFormatter(Duration.between(getStartupTime(), now())));
  }

  @RequiredArgsConstructor
  public static class Memory {
    @Getter private final double bytes;
    private final String[] names = new String[] {"B", "KB", "MB", "GB"};
    private final DoubleSupplier[] getters =
        new DoubleSupplier[] {
          this::getBytes, this::toKilobytes, this::toMegabytes, this::toGigabytes
        };

    public double toKilobytes() {
      return bytes / 1024.0;
    }

    public double toMegabytes() {
      return toKilobytes() / 1024.0;
    }

    public double toGigabytes() {
      return toMegabytes() / 1024.0;
    }

    public String humanize() {
      for (int i = 0; i < names.length; i++) {
        double value = getters[i].getAsDouble();
        if (value < 1024 || i == names.length - 1) {
          return String.format("%.2f%s", value, names[i]);
        }
      }
      throw new IllegalStateException();
    }
  }
}
