package io.github.antoniotirello.migrationtool.infrastructure.logging.database

import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogReader
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.RunId
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.Runs
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedLogReader(
    private val databaseConfig: DatabaseRunContext
): LogReader {
    override fun getAllRuns(): List<RunsResults> {
        return transaction {
            Runs.selectAll()
                .orderBy(Runs.id to SortOrder.DESC)
                .map {
                    RunsResults(
                        id = RunId(it[Runs.id].value),
                        startedAt = it[Runs.startedAt],
                        endedAt = it[Runs.endedAt],
                        environment = it[Runs.environment],
                        lastRun = it[Runs.id].value == databaseConfig.currentRunId().value
                    )
                }
        }
    }
}