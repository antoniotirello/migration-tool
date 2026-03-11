package io.github.antoniotirello.migrationtool.logging.dto

import java.time.LocalDateTime

data class LogEntryEvent(
    val id: Long? = null,
    val timestamp: LocalDateTime? = null,
    val level: Int? = null,
    val message: String? = null,
    val event: String? = null,
    val payload: String? = null,
)