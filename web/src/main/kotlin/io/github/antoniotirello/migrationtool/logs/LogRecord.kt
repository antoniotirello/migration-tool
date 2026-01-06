package io.github.antoniotirello.migrationtool.logs

import kotlinx.serialization.Serializable

@Serializable
data class LogRecord(
    val timestamp: Long,
    val runId: String,
    val event: LogEventType? = null,
    val level: LogLevel,
    val message: String
)