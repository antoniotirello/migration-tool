package io.github.antoniotirello.migrationtool.application.web.service

import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.dto.PagedResponse
import io.github.antoniotirello.migrationtool.logging.api.LogReader
import io.github.antoniotirello.migrationtool.logging.dto.LogEntryEvent
import io.github.antoniotirello.migrationtool.logging.dto.RunsResults
import org.springframework.stereotype.Service

@Service
class LogReaderService(
    private val logReader: LogReader,
    private val context: AppContext,
) {
    fun getAllRuns(page: Int?, size: Int?): PagedResponse<RunsResults> {
        return logReader.getAllRuns(page, size)
    }

    fun getRunDetail(runId: Long?): List<LogEntryEvent> {
        val effectiveRunId = runId ?: context.getRunId().value

        println("effectiveRunId: $effectiveRunId")
        println("getRunId: ${context.getRunId().value}")

        return logReader.getRunDetail(effectiveRunId)
    }
}