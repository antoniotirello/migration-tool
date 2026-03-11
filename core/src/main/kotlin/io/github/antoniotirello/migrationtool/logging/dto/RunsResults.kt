package io.github.antoniotirello.migrationtool.logging.dto

import java.time.LocalDateTime

data class RunsResults(
    val id: Long,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime?,
    val environment: String?,
    val lastRun: Boolean
)