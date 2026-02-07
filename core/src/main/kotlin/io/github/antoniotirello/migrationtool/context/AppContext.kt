package io.github.antoniotirello.migrationtool.context

import io.github.antoniotirello.migrationtool.logging.dao.RunId
import org.jetbrains.exposed.v1.jdbc.Database

data class AppContext (
    val appVersion: String,
    val database: Database,
    val currentRunId: RunId? = null,
    //val logger: Logger,
) {
    fun getRunId(): RunId =
        currentRunId ?: error("Run non inizializzato")
}