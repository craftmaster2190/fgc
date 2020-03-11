package com.craftmaster.lds.fgc.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!test")
public class PostgresSubscriptions {
  private final DataSource dataSource;
  private final JdbcTemplate jdbcTemplate;
  private final ObjectMapper objectMapper;
  private final ExecutorService executorService = Executors.newFixedThreadPool(4);

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

  private final Map<String, TypeConsumers<?>> topic2Type = new ConcurrentHashMap<>();

  private PGConnection getConnection() {
    try {
      return dataSource.getConnection().unwrap(PGConnection.class);
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
                  PGConnection pgConnection = getConnection();
                  pgConnection.addNotificationListener(
                      new PGNotificationListener() {
                        @Override
                        public void notification(
                            int processId, String channelName, String payload) {
                          log.debug("Postgres Message: {} {} {}", processId, channelName, payload);
                          executorService.submit(
                              () -> topic2Type.get(channelName).sendPayload(payload));
                        }

                        @Override
                        public void closed() {
                          log.warn("{} listener closed!", normalizedTopicName);
                        }
                      });
                  try (Statement statement = pgConnection.createStatement()) {
                    statement.execute("LISTEN " + normalizedTopicName + ";");
                  } catch (SQLException e) {
                    throw new RuntimeException(e);
                  }
                  return new TypeConsumers<T>(clazz);
                })
            .getConsumers();
    ((Set<Consumer<T>>) consumers).add(subscription);
  }

  public <T> void send(String topicName, T payload) {
    try {
      String payloadString = objectMapper.writeValueAsString(payload);
      Assert.isTrue(
          !payloadString.contains("'"),
          () -> "Payload must not contain ' (single quotes): " + payloadString);
      jdbcTemplate.execute("NOTIFY " + topicName.toLowerCase() + ", '" + payloadString + "';");
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
