package io.github.antoniotirello.migrationtool.logging.api

interface Logger {
    fun write(
        level: LogLevel,
        message: String,
        event: LogEvent? = null,
        payload: String? = null
    )
}