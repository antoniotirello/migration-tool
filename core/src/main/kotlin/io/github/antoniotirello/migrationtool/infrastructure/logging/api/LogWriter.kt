package io.github.antoniotirello.migrationtool.infrastructure.logging.api

interface LogWriter {
    fun log(
        level: LogLevel,
        message: String,
        payload: String? = null,
        event: LogEvent? = null
    )
}