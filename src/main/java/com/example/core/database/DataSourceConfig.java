package com.example.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

// Một class annotated với @Configuration trong Spring Boot tự động được Spring quét (scan) và đưa
// các @Bean bên trong vào ApplicationContext.
// Vì vậy, bạn sẽ không thấy có code nào gọi trực tiếp new DataSourceConfig() cả.
// Thay vào đó, các bean mà bạn khai báo trong DataSourceConfig (ví dụ DataSource,
// EntityManagerFactory, TransactionManager) sẽ được Spring Boot inject vào chỗ khác khi cần
@Configuration
@Slf4j
public class DataSourceConfig {
  public DataSourceConfig() {
    log.info("DataSourceConfig");
  }

  @Bean
  @ConfigurationProperties("database.writer")
  public DataSourceProperties writerProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("database.reader")
  //    Gắn @Primary cho writer để mặc định JPA dùng writer.
  @Primary
  public DataSourceProperties readerProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.hikari")
  public HikariConfig hikariConfig() {
    return new HikariConfig();
  }

  private HikariDataSource write(DataSourceProperties writerProperties, HikariConfig hikariConfig) {
    hikariConfig.setJdbcUrl(writerProperties.getUrl());
    hikariConfig.setUsername(writerProperties.getUsername());
    hikariConfig.setPassword(writerProperties.getPassword());
    hikariConfig.setPoolName("WRITER-POOL");
    return new HikariDataSource(hikariConfig);
  }

  private HikariDataSource read(DataSourceProperties readerProperties, HikariConfig hikariConfig) {
    hikariConfig.setJdbcUrl(readerProperties.getUrl());
    hikariConfig.setUsername(readerProperties.getUsername());
    hikariConfig.setPassword(readerProperties.getPassword());
    hikariConfig.setPoolName("READER-POOL");
    return new HikariDataSource(hikariConfig);
  }

  @Bean
  public RoutingDataSource dataSource(
      @Qualifier("writerProperties") DataSourceProperties writerProperties,
      @Qualifier("readerProperties") DataSourceProperties readerProperties) {
    DataSource write = write(writerProperties, hikariConfig());
    DataSource read = read(readerProperties, hikariConfig());
    return new RoutingDataSource(write, read);
  }

  @Bean
  public RoutingDataSourceInterceptor routingDataSourceInterceptor() {
    return new RoutingDataSourceInterceptor();
  }
}
