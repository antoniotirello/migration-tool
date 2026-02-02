package io.github.antoniotirello.migrationtool.infrastructure.logging.database

import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogEvent
import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogLevel
import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogWriter
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.LogEntry
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.Run
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedLogWriter(
    private val databaseConfig: DatabaseRunContext
) : LogWriter {

    override fun log(level: LogLevel, message: String, payload: String?, event: LogEvent?) {
        val runId = databaseConfig.currentRunId()

        transaction {
            val run = Run.findById(runId.value)
                ?: error("Run $runId non trovata")

            LogEntry.new {
                this.runId = run
                this.level = level.severity
                this.message = message
                this.payload = payload
                this.event = event?.name
            }
        }
    }
}