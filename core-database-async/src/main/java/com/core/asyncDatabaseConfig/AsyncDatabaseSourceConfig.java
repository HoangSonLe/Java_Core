package com.core.asyncDatabaseConfig;

import com.example.core.database.DatabaseProperties;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.core.asyncDatabaseConfig")
@EnableConfigurationProperties(DatabaseProperties.class)
public class AsyncDatabaseSourceConfig {

  @Bean
  public ConnectionFactory readConnectionFactory(DatabaseProperties databaseProperties) {
    DatabaseProperties.DataSourceProperties reader = databaseProperties.getReader();
    ConnectionFactoryOptions options =
        ConnectionFactoryOptions.parse(reader.getUrl())
            .mutate()
            .option(ConnectionFactoryOptions.USER, reader.getUsername())
            .option(ConnectionFactoryOptions.PASSWORD, reader.getPassword())
            .build();
    return ConnectionFactories.get(options);
  }

  @Bean
  public ConnectionFactory writeConnectionFactory(DatabaseProperties databaseProperties) {
    DatabaseProperties.DataSourceProperties writer = databaseProperties.getWriter();
    ConnectionFactoryOptions options =
        ConnectionFactoryOptions.parse(writer.getUrl())
            .mutate()
            .option(ConnectionFactoryOptions.USER, writer.getUsername())
            .option(ConnectionFactoryOptions.PASSWORD, writer.getPassword())
            .build();
    return ConnectionFactories.get(options);
  }

  @Bean
  @Primary
  public ConnectionFactory connectionFactory(
      ConnectionFactory readConnectionFactory, ConnectionFactory writeConnectionFactory) {
    return new RoutingDataSource(readConnectionFactory, writeConnectionFactory);
  }

  @Bean
  public ReactiveTransactionManager reactiveTransactionManager(
      ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }
}
