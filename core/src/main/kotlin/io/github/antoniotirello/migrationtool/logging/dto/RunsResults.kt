package io.github.antoniotirello.migrationtool.logging.dto

import io.github.antoniotirello.migrationtool.logging.dao.RunId
import java.time.LocalDateTime

data class RunsResults(
    val id: RunId,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime?,
    val environment: String?,
    val lastRun: Boolean
)