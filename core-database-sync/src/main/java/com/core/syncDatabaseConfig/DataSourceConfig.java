package com.core.syncDatabaseConfig;

import com.example.core.database.DatabaseProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

// Một class annotated với @Configuration trong Spring Boot tự động được Spring quét (scan) và đưa
// các @Bean bên trong vào ApplicationContext.
// Vì vậy, bạn sẽ không thấy có code nào gọi trực tiếp new DataSourceConfig() cả.
// Thay vào đó, các bean mà bạn khai báo trong DataSourceConfig (ví dụ DataSource,
// EntityManagerFactory, TransactionManager) sẽ được Spring Boot inject vào chỗ khác khi cần
@Configuration
@Slf4j
@EnableConfigurationProperties(DatabaseProperties.class)
public class DataSourceConfig {
    private final DatabaseProperties databaseProperties;

    public DataSourceConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
        log.info("DataSourceConfig initialized");
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    private HikariDataSource createDataSource(DatabaseProperties.DataSourceProperties props, String poolName) {
        HikariConfig config = hikariConfig();
        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setPoolName(poolName);
        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    public RoutingDataSource dataSource() {
        DataSource write = createDataSource(databaseProperties.getWriter(), "WRITER-POOL");
        DataSource read = createDataSource(databaseProperties.getReader(), "READER-POOL");
        return new RoutingDataSource(write, read);
    }

    @Bean
    public RoutingDataSourceInterceptor routingDataSourceInterceptor() {
        return new RoutingDataSourceInterceptor();
    }
}
