package io.github.antoniotirello.migrationtool.context

import io.github.antoniotirello.migrationtool.events.EventBus
import io.github.antoniotirello.migrationtool.logging.dao.RunId
import io.github.antoniotirello.migrationtool.logging.dto.LogEntryEvent
import org.jetbrains.exposed.v1.jdbc.Database

data class AppContext (
    val appVersion: String,
    val database: Database,
    val eventBus: EventBus<LogEntryEvent>,
    val currentRunId: RunId? = null,
) {
    fun getRunId(): RunId =
        currentRunId ?: error("Run not started yet!")
}