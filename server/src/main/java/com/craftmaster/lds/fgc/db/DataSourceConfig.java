package com.craftmaster.lds.fgc.db;

import com.impossibl.postgres.jdbc.PGDataSource;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Setter
@Configuration
@ConfigurationProperties("fgc.db")
@Profile("!test")
public class DataSourceConfig {

  private String driver;
  private String url;
  private String username;
  private String password;

  @Bean
  @ConditionalOnProperty("fgc.db.url")
  public PGDataSource getDataSource() {
    PGDataSource pgDataSource = new PGDataSource();
    pgDataSource.setDatabaseUrl(url);
    pgDataSource.setUser(username);
    pgDataSource.setPassword(password);
    return pgDataSource;
  }
}
