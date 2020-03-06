package com.craftmaster.lds.fgc.db;

import javax.sql.DataSource;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties("fgc.db")
public class DataSourceConfig {

  private String driver;
  private String url;
  private String username;
  private String password;

  @Bean
  @ConditionalOnProperty("fgc.db.url")
  public DataSource getDataSource() {
    DataSourceBuilder<? extends DataSource> dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.driverClassName(driver);
    dataSourceBuilder.url(url);
    dataSourceBuilder.username(username);
    dataSourceBuilder.password(password);
    return dataSourceBuilder.build();
  }
}
