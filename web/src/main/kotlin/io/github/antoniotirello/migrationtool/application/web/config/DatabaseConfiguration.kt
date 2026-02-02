package io.github.antoniotirello.migrationtool.application.web.config

import io.github.antoniotirello.migrationtool.application.web.service.InfoService
import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogEvent
import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogLevel
import io.github.antoniotirello.migrationtool.infrastructure.logging.api.Logger
import io.github.antoniotirello.migrationtool.infrastructure.logging.database.DatabaseConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

@Component
class RunIdProvider (
    private val infoService: InfoService
) {
    @Value($$"${BACKEND_CONFIG_PROJECT_PATH}")
    lateinit var projectPath: String

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        val homeDir = System.getProperty("user.home")

        val baseDir = Paths.get(homeDir, ".migrationtool")
        val projectDir = baseDir.resolve(projectHash(projectPath))

        Files.createDirectories(projectDir)

        val dbFilePath = projectDir.resolve("web-app.db").toAbsolutePath().toString()

        DatabaseConfig.init(
            dbPath = dbFilePath,
            appVersion = infoService.getInfo().version,
            triggeredBy = "web BE",
        )

        Logger.log(LogLevel.INFO, "Run started", event = LogEvent.RUN_STARTED)
    }

    private fun projectHash(projectPath: String): String {
        val normalized = Paths.get(projectPath)
            .toAbsolutePath()
            .normalize()
            .toString()

        val digest = MessageDigest
            .getInstance("SHA-256")
            .digest(normalized.toByteArray(Charsets.UTF_8))

        return digest
            .take(16) // 128 bit sono più che sufficienti
            .joinToString("") { "%02x".format(it) }
    }
}