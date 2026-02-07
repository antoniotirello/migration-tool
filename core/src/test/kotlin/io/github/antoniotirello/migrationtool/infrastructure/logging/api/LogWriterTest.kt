package io.github.antoniotirello.migrationtool.infrastructure.logging.api

import io.github.antoniotirello.migrationtool.infrastructure.logging.TestContextAwareHelper
import io.github.antoniotirello.migrationtool.logging.api.LogLevel
import io.github.antoniotirello.migrationtool.logging.api.LogWriter
import io.github.antoniotirello.migrationtool.logging.dao.LogEntries
import io.github.antoniotirello.migrationtool.logging.dao.LogEntry
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Test

class LogWriterTest: TestContextAwareHelper() {
    @Test
    fun `should write log entries into database`() {
        val logger = LogWriter(runtimeContext)
        val runId = runtimeContext.getRunId().value

        logger.write(LogLevel.INFO, "Test")

        val logsForRun = transaction(runtimeContext.database) {
            LogEntry.find { LogEntries.runId eq runId }
                .toList()
        }

        assert(logsForRun.count() == 1)
    }
}