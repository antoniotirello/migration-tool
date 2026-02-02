package io.github.antoniotirello.migrationtool.infrastructure.logging.database

import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.RunId
import java.time.LocalDateTime

data class RunsResults(
    val id: RunId,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime?,
    val environment: String?,
    val lastRun: Boolean
)