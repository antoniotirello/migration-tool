package io.github.antoniotirello.migrationtool.application.web.service

import io.github.antoniotirello.migrationtool.dto.PagedResponse
import io.github.antoniotirello.migrationtool.logging.api.LogReader
import io.github.antoniotirello.migrationtool.logging.dto.RunsResults
import org.springframework.stereotype.Service

@Service
class LogReaderService(
    private val logReader: LogReader
) {
    fun getAllRuns(page: Int?, size: Int?): PagedResponse<RunsResults> {
        return logReader.getAllRuns(page, size)
    }
}