package io.github.antoniotirello.migrationtool.logs

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.extension
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

class LogService(
    private val logDirectory: Path,
    private val logFile: Path,
    private val runId: String
) {
    private val json = Json { encodeDefaults = true }

    /**
     * Appends a single log record.
     *
     * This method is intentionally strict:
     * failures while serializing or writing logs
     * are propagated to the caller.
     */
    fun append(message: String, level: LogLevel = LogLevel.INFO, eventType: LogEventType? = null) {

        val record = LogRecord(
            timestamp = Instant.now().toEpochMilli(),
            level = level,
            message = message,
            event = eventType,
            runId = runId,
        )

        Files.writeString(
            logFile,
            json.encodeToString(record) + "\n",
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.APPEND
        )
    }

    /**
     * Analyze a file returning a LogFileDto.
     * Invalid JSON line are returned in the skippedLines counter.
     */
    fun getLogSummary(currentLogFile: Path): LogFileDto {
        val json = Json { ignoreUnknownKeys = true } // To be tolerant when reading log files
        var hasRunCompleted = false
        var lineCount = 0
        var isCurrentFile = false
        var skippedLines = 0

        currentLogFile.toFile().useLines { lines ->
            lines.forEach { line ->
                try {

                    val entry = json.decodeFromString<LogRecord>(line)

                    if (entry.event == LogEventType.RUN_COMPLETED) {
                        hasRunCompleted = true
                    }

                    if (entry.runId == runId) {
                        isCurrentFile = true
                    }

                    lineCount++
                } catch (e: SerializationException) {
                    skippedLines++
                }
            }
        }

        return LogFileDto(
            fileName = currentLogFile.fileName.toString(),
            isSuccess = hasRunCompleted,
            logEntries = lineCount,
            isCurrent = isCurrentFile,
            skippedLines = skippedLines,
        )
    }

    /**
     * Get the whole list of logs, skipping non "jsonl" files and returning a summary for each one.
     */
    fun readAll(): List<LogFileDto> {
        if (!Files.exists(logFile)) return emptyList()

        val files: List<LogFileDto> = logDirectory.listDirectoryEntries()
            .sortedByDescending { it.getLastModifiedTime().toMillis() }
            .filter { it.isRegularFile() && it.extension == "jsonl" }
            .map { file ->
                getLogSummary(file)
            }

        return files
    }
}
