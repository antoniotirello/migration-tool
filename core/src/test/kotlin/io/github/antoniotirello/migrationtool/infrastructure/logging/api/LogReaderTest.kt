package io.github.antoniotirello.migrationtool.infrastructure.logging.api

import io.github.antoniotirello.migrationtool.infrastructure.logging.TestContextAwareHelper
import io.github.antoniotirello.migrationtool.logging.api.LogReader
import io.github.antoniotirello.migrationtool.logging.dao.Run
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Test

class LogReaderTest: TestContextAwareHelper() {
    @Test
    fun `should read log entries from database`() {
        val reader = LogReader(runtimeContext)

        transaction(runtimeContext.database) {
            Run.new {
                this.appVersion = runtimeContext.appVersion
            }
            Run.new {
                this.appVersion = runtimeContext.appVersion
            }
        }

        val runList = reader.getAllRuns()
        assert(runList.isNotEmpty())
        assert(runList.size == 3) {"Actual size is ${runList.size}"}
    }
}