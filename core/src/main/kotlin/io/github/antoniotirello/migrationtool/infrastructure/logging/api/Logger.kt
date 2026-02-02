package io.github.antoniotirello.migrationtool.infrastructure.logging.api

object Logger {
    lateinit var writer: LogWriter

    fun log(
        level: LogLevel,
        message: String,
        payload: String? = null,
        event: LogEvent? = null
    ) {
        writer.log(level, message, payload, event)
    }
}