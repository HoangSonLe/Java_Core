package com.core.syncDatabaseConfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * Auto-configuration for sync database module.
 * This will be automatically loaded when the module is included in a project.
 * It runs before DataSourceAutoConfiguration and provides custom DataSource configuration.
 */
@AutoConfiguration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@ConditionalOnClass(DataSource.class)
@Import(DataSourceConfig.class)
public class SyncDatabaseAutoConfiguration {
}
