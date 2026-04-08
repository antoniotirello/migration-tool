package io.github.antoniotirello.migrationtool.application.web.config
import io.github.antoniotirello.migrationtool.logging.api.LogWriter
import io.github.antoniotirello.migrationtool.migration.service.TemplateService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TemplateConfiguration {
    @Value($$"${BACKEND_CONFIG_PROJECT_PATH:/tmp/default}")
    lateinit var projectPath: String

    @Bean
    fun templateService(logger: LogWriter): TemplateService =
        TemplateService(logger, projectPath)
}