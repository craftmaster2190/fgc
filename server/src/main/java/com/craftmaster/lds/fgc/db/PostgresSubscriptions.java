package com.craftmaster.lds.fgc.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Suppliers;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!test")
@ConfigurationProperties("fgc.db")
public class PostgresSubscriptions {
  private final ObjectMapper objectMapper;
  private final ExecutorService executorService = Executors.newFixedThreadPool(4);
  private final Map<String, TypeConsumers<?>> topic2Type = new ConcurrentHashMap<>();
  private volatile PGConnection connection;
  private volatile boolean isShutdown;

  @Setter private String url;
  @Setter private String username;
  @Setter private String password;

  private final Supplier<PGDataSource> pgDataSource =
      Suppliers.memoize(
          () -> {
            PGDataSource pgDataSource = new PGDataSource();
            pgDataSource.setDatabaseUrl(url);
            pgDataSource.setUser(username);
            pgDataSource.setPassword(password);
            return pgDataSource;
          });

  @PreDestroy
  public void onDestroy() throws SQLException {
    isShutdown = true;
    if (connection != null) {
      log.warn("Closing postgres connection.");
      connection.close();
    }
  }

  @Synchronized
  private PGConnection getConnection() {
    if (isShutdown) {
      return null;
    }
    try {
      if (connection == null || connection.isClosed()) {
        connection = pgDataSource.get().getConnection().unwrap(PGConnection.class);
        topic2Type.keySet().forEach(this::refreshListener);
      }
      return connection;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> void subscribe(String topicName, Class<T> clazz, Consumer<T> subscription) {
    String normalizedTopicName = topicName.toLowerCase();

    Set<? extends Consumer<?>> consumers =
        topic2Type
            .computeIfAbsent(
                normalizedTopicName,
                ignored -> {
                  refreshListener(normalizedTopicName);
                  return new TypeConsumers<T>(clazz);
                })
            .getConsumers();
    if (subscription != null) {
      ((Set<Consumer<T>>) consumers).add(subscription);
    }
  }

  private void refreshListener(String normalizedTopicName) {
    PGConnection pgConnection = getConnection();
    pgConnection.addNotificationListener(
        new PGNotificationListener() {
          @Override
          public void notification(int processId, String channelName, String payload) {
            log.debug("Postgres Message: {} {} {}", processId, channelName, payload);
            executorService.submit(() -> topic2Type.get(channelName).sendPayload(payload));
          }

          @Override
          public void closed() {
            log.warn("{} listener closed!", normalizedTopicName);
            getConnection();
          }
        });
    try (Statement statement = pgConnection.createStatement()) {
      statement.execute("LISTEN " + normalizedTopicName + ";");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> void send(String topicName, T payload) {
    try {
      String payloadString = objectMapper.writeValueAsString(payload);
      Assert.isTrue(
          !payloadString.contains("'"),
          () -> "Payload must not contain ' (single quotes): " + payloadString);
      try (Statement statement = getConnection().createStatement()) {
        statement.execute("NOTIFY " + topicName.toLowerCase() + ", '" + payloadString + "';");
      }
    } catch (IOException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Value
  private static class TypeConsumers<T> {
    private final Class<T> type;
    private final Set<Consumer<T>> consumers = ConcurrentHashMap.newKeySet();

    public void sendPayload(String payload) {
      T payloadObj = null;
      if (payload != null) {
        try {
          payloadObj = ObjectMapperHolder.get().readValue(payload, getType());
        } catch (IOException e) {
          log.error("Unable to parse {}", payload, e);
        }
      }
      T finalPayloadObj = payloadObj;
      consumers.forEach(subscription -> subscription.accept(finalPayloadObj));
    }
  }
}
