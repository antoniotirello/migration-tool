package io.github.antoniotirello.migrationtool.logs

import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createDirectories

object LogServiceWrapper {
    private var instance: LogService? = null
    private val RUN_ID_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss")

    /**
     * Deve essere chiamato UNA volta all'avvio del tool
     */
    fun init(projectRootPath: Path) {
        val dir = projectRootPath.resolve(".migration-tool").resolve("logs")
        dir.createDirectories()

        val runId: String =
            LocalDateTime.now().format(RUN_ID_FORMATTER)

        val file = dir.resolve("run-$runId.jsonl")

        instance = LogService(dir, file, runId)
    }

    fun get(): LogService =
        instance ?: error("LogService not initialized. Call init() first")
}