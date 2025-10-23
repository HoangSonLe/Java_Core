package com.core.asyncDatabaseConfig;

import com.example.core.database.DatabaseType;
import io.r2dbc.spi.ConnectionFactory;
import java.util.Map;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

public class RoutingDataSource extends AbstractRoutingConnectionFactory {

  public RoutingDataSource(
      ConnectionFactory readConnectionFactory, ConnectionFactory writeConnectionFactory) {
    Map<Object, ConnectionFactory> targetConnectionFactories =
        Map.of(
            DatabaseType.READ, readConnectionFactory,
            DatabaseType.WRITE, writeConnectionFactory);
    this.setTargetConnectionFactories(targetConnectionFactories);
    this.setDefaultTargetConnectionFactory(readConnectionFactory);
    this.afterPropertiesSet();
  }

  @Override
  protected Mono<Object> determineCurrentLookupKey() {
    return TransactionSynchronizationManager.forCurrentTransaction()
        .filter(TransactionSynchronizationManager::isActualTransactionActive)
        .map(tsm -> tsm.isCurrentTransactionReadOnly() ? DatabaseType.READ : DatabaseType.WRITE)
        .cast(Object.class)
        .defaultIfEmpty(DatabaseType.READ);
  }
}
