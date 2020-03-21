package com.craftmaster.lds.fgc.config;

import com.craftmaster.lds.fgc.db.TransactionalContext;
import com.craftmaster.lds.fgc.user.NotAcceptingNewUsersException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigService {
  private final ObjectMapper objectMapper;
  private final TransactionalContext transactionalContext;
  private final ConfigPropertyRepository configPropertyRepository;

  public Map<String, String> getAll() {
    return configPropertyRepository.findAll().stream()
        .collect(Collectors.toMap(ConfigProperty::getKey, ConfigProperty::getValue));
  }

  @Transactional
  public <T> T getOrCreate(String key, T defaultValue, Class<T> clazz) {
    var config = configPropertyRepository.findById(key);
    if (config.isPresent()) {
      try {
        return objectMapper.readValue(config.get().getValue(), clazz);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    put(key, defaultValue);
    return defaultValue;
  }

  public <T> ConfigProperty put(String key, T value) {
    try {
      return configPropertyRepository.save(
          new ConfigProperty().setKey(key).setValue(objectMapper.writeValueAsString(value)));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String key) {
    configPropertyRepository.deleteById(key);
  }

  public boolean isAcceptingNewUsers() {
    return transactionalContext.run(() -> getOrCreate("accepting-new-users", true, Boolean.class));
  }

  public void validateAcceptingNewUsers() {
    if (!isAcceptingNewUsers()) {
      throw new NotAcceptingNewUsersException();
    }
  }

  public List<String> getServerMessages() {
    return configPropertyRepository.findByKeyStartsWith("server-message-").stream()
        .map(ConfigProperty::getValue)
        .collect(Collectors.toList());
  }

  public void addServerMessage(String message) {
    put("server-message-" + Instant.now().toString(), message);
  }

  @Transactional
  public boolean toggleCanChangeFamily() {
    var canChangeFamily = !getCanChangeFamily();
    put("can-change-family", canChangeFamily);
    return canChangeFamily;
  }

  public boolean getCanChangeFamily() {
    return transactionalContext.run(() -> getOrCreate("can-change-family", true, Boolean.class));
  }
}
