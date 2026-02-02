package io.github.antoniotirello.migrationtool.application.web.config

import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogReader
import io.github.antoniotirello.migrationtool.infrastructure.logging.database.DatabaseConfig
import io.github.antoniotirello.migrationtool.infrastructure.logging.database.DatabaseRunContext
import io.github.antoniotirello.migrationtool.infrastructure.logging.database.ExposedLogReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggingConfiguration {
    @Bean
    fun logReader(runContext: DatabaseRunContext): LogReader =
        ExposedLogReader(runContext)

    @Bean
    fun databaseRunContext() : DatabaseRunContext =
        DatabaseRunContext(DatabaseConfig)
}