package io.github.antoniotirello.migrationtool.logging.api

import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.dto.PagedResponse
import io.github.antoniotirello.migrationtool.logging.dao.LogEntries
import io.github.antoniotirello.migrationtool.logging.dao.Runs
import io.github.antoniotirello.migrationtool.logging.dto.LogEntryEvent
import io.github.antoniotirello.migrationtool.logging.dto.RunsResults

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class LogReader(
    private val context: AppContext
) {
    fun getAllRuns(page: Int? = null, size: Int? = null): PagedResponse<RunsResults> = transaction(context.database) {
        val pageNum = page ?: 0
        val pageSize = size ?: 10

        val total = Runs.selectAll().count()

        val rows = Runs.selectAll()
            .orderBy(Runs.id to SortOrder.DESC)
            .limit(pageSize)
            .offset(pageNum.toLong() * pageSize)
            .map {
                RunsResults(
                    id = it[Runs.id].value,
                    startedAt = it[Runs.startedAt],
                    endedAt = it[Runs.endedAt],
                    environment = it[Runs.environment],
                    lastRun = it[Runs.id].value == context.getRunId().value
                )
            }

        PagedResponse(
            content = rows,
            page = pageNum,
            size = pageSize,
            totalElements = total,
            totalPages = ((total + pageSize - 1) / pageSize).toInt()
        )
    }

    fun getRunDetail(runId: Long): List<LogEntryEvent> =
        transaction(context.database) {
            LogEntries
                .selectAll()
                .where { LogEntries.runId eq runId }
                .orderBy(LogEntries.timestamp)
//                .map { it.toString() }
                .map { row ->
                    LogEntryEvent(
                        id = row[LogEntries.id].value,
                        timestamp = row[LogEntries.timestamp],
                        level = row[LogEntries.level],
                        message = row[LogEntries.message],
                        event = row[LogEntries.event],
                        payload = row[LogEntries.payload]
                    )
                }
        }
}