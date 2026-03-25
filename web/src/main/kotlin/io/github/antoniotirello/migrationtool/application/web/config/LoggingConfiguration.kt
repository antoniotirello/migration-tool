package io.github.antoniotirello.migrationtool.application.web.config

import io.github.antoniotirello.migrationtool.MigrationToolInfo
import io.github.antoniotirello.migrationtool.context.AppBootstrap
import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.context.AppContextBuilder
import io.github.antoniotirello.migrationtool.logging.api.LogReader
import io.github.antoniotirello.migrationtool.logging.api.LogWriter
import io.github.antoniotirello.migrationtool.paths.AppPaths
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class LoggingConfiguration {

    @Value($$"${BACKEND_CONFIG_PROJECT_PATH:/tmp/default}")
    lateinit var projectPath: String

    @Bean
    fun logReader(runContext: AppContext): LogReader =
        LogReader(runContext)

    @Bean
    fun logWriter(runContext: AppContext): LogWriter =
        LogWriter(runContext)

    @Bean
    @Profile("!test")
    fun databaseRunContext() : AppContext {
        val appVersion = MigrationToolInfo.VERSION

        val context = AppContextBuilder()
            .useSqliteFile(
                AppPaths.sqliteDbFile(projectPath).toAbsolutePath().toString()
            )
            .withVersion(appVersion)
            .build()

        return AppBootstrap.init(context)
    }
}