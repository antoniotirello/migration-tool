package io.github.antoniotirello.migrationtool.context

import io.github.antoniotirello.migrationtool.logging.dao.Run
import io.github.antoniotirello.migrationtool.logging.database.SchemaManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AppBootstrapTest  {

    @Test
    fun `should create tables and insert a new run id`() {
        val appVersion = "69.42"
        val context = AppContextBuilder()
            .useH2InMemory()
            .withVersion(appVersion)
            .build()

        val runtimeContext = AppBootstrap.init(context)
        val runId = runtimeContext.getRunId()

        val insertedRunId = runId.value
        assert(insertedRunId > 0L) { "Run ID should be greater than 0: $insertedRunId" }

        val runFromDb = transaction(context.database) {
            Run.findById(insertedRunId)
        }

        assert(runFromDb != null) { "Run should exist in the database" }
        assertEquals(appVersion, runFromDb?.appVersion)

        SchemaManager().dropAll(runtimeContext.database)
    }
}