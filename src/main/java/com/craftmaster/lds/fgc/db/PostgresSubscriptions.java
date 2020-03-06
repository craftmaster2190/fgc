package com.craftmaster.lds.fgc.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresSubscriptions {
  private final DataSource dataSource;
  private final ObjectMapper objectMapper;

  @Value
  private static class TypeConsumers<T> {
    private final TypeReference<T> type;
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

  @PostConstruct
  public void setupPostgresListener() throws SQLException {
    PGConnection pgConnection = getConnection();
    pgConnection.addNotificationListener(new PGNotificationListener() {
      @Override
      public void notification(int processId, String channelName, String payload) {
        log.trace("Postgres Message: {} {} {}", processId, channelName, payload);
        topic2Type.get(channelName).sendPayload(payload);
      }

      @Override
      public void closed() {
        log.warn("Listener closed!");
      }
    });
  }

  private PGConnection getConnection() {
    try {
      return dataSource.getConnection().unwrap(PGConnection.class);
    } catch (SQLException e) {
      throw new RuntimeException(e)
        ;
    }
  }

  private void executeSQL(String sql) {
    try {
      getConnection().createStatement().execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e)
        ;
    }
  }

  public <T> void subscribe(String topicName, Consumer<T> subscription) {
    TypeReference<T> typeReference = new TypeReference<>() {
    };
    String normalizedTopicName = topicName.toLowerCase();

    Set<? extends Consumer<?>> consumers = topic2Type.computeIfAbsent(normalizedTopicName, ignored -> {
      executeSQL("LISTEN " + normalizedTopicName + ";");
      return new TypeConsumers<T>(typeReference);
    })
      .getConsumers();
    ((Set<Consumer<T>>) consumers).add(subscription);
  }

  public <T> void send(String topicName, T payload) {
    try {
      String payloadString = objectMapper.writeValueAsString(payload);
      Assert.isTrue(!payloadString.contains("'"), () -> "Payload must not contain ' (single quotes): " + payloadString);
      executeSQL("NOTIFY " + topicName + ", '" + payloadString + "';");
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
