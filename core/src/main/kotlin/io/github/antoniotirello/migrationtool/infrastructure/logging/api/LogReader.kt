package io.github.antoniotirello.migrationtool.infrastructure.logging.api

import io.github.antoniotirello.migrationtool.infrastructure.logging.database.RunsResults

interface LogReader {
    fun getAllRuns(): List<RunsResults>
}