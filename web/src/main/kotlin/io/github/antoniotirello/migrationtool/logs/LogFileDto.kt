package io.github.antoniotirello.migrationtool.logs

import kotlinx.serialization.Serializable

@Serializable
data class LogFileDto(
    val fileName: String,
    val isSuccess: Boolean,
    val isCurrent: Boolean,
    val logEntries: Int,
    val skippedLines: Int,
)
