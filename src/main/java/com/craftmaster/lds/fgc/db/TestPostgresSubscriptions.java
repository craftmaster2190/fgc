package com.craftmaster.lds.fgc.db;

import java.sql.SQLException;
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
      public void setupPostgresListener() throws SQLException {}

      @Override
      public <T> void subscribe(String topicName, Consumer<T> subscription) {}

      @Override
      public <T> void send(String topicName, T payload) {}
    };
  }
}
