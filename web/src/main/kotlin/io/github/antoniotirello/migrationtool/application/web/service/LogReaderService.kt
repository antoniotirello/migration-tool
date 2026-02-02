package io.github.antoniotirello.migrationtool.application.web.service

import io.github.antoniotirello.migrationtool.infrastructure.logging.api.LogReader
import io.github.antoniotirello.migrationtool.infrastructure.logging.database.RunsResults
import org.springframework.stereotype.Service

@Service
class LogReaderService(
    private val logReader: LogReader
) {
    fun getAllRuns(): List<RunsResults> {
        return logReader.getAllRuns()
    }
}