package io.github.antoniotirello.migrationtool.logging.api

import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.logging.dao.RunId
import io.github.antoniotirello.migrationtool.logging.dao.Runs
import io.github.antoniotirello.migrationtool.logging.dto.RunsResults
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class LogReader(
    private val context: AppContext
) {
    fun getAllRuns(): List<RunsResults> {
        return transaction(context.database) {
            Runs.selectAll()
                .orderBy(Runs.id to SortOrder.DESC)
                .map {
                    RunsResults(
                        id = RunId(it[Runs.id].value),
                        startedAt = it[Runs.startedAt],
                        endedAt = it[Runs.endedAt],
                        environment = it[Runs.environment],
                        lastRun = it[Runs.id].value == context.getRunId().value
                    )
                }
        }
    }
}