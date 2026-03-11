package io.github.antoniotirello.migrationtool.logging.api

import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.logging.dao.LogEntry
import io.github.antoniotirello.migrationtool.logging.dao.Run
import io.github.antoniotirello.migrationtool.logging.dto.LogEntryEvent
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class LogWriter(
    private val context: AppContext
) {

    fun write(
        level: LogLevel,
        message: String,
        event: LogEvent? = null,
        payload: String? = null
    ) {
        val runId = checkNotNull(context.currentRunId) {
            "Run not initialised. Call AppBootstrap.init()"
        }

        transaction(context.database) {
            val entry = LogEntry.new {
                this.runId = Run[runId.value]
                this.level = level.severity
                this.message = message
                this.event = event?.name
                this.payload = payload
            }

            context.eventBus.publish(LogEntryEvent(
                id = entry.id.value,
                timestamp = entry.timestamp,
                level = entry.level,
                message = entry.message,
                event = entry.event,
                payload = entry.payload
            ))
        }
    }
}