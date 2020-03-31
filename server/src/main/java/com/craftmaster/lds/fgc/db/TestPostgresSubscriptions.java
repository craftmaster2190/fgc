package com.craftmaster.lds.fgc.db;

import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestPostgresSubscriptions {

  @Bean
  public PostgresSubscriptions postgresSubscriptions() {
    return new PostgresSubscriptions(null, null) {
      @Override
      public <T> void subscribe(String topicName, Class<T> clazz, Consumer<T> subscription) {}

      @Override
      public <T> void send(String topicName, T payload) {}
    };
  }
}
