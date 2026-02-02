package io.github.antoniotirello.migrationtool.infrastructure.logging.database

import io.github.antoniotirello.migrationtool.infrastructure.logging.api.RunContext
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.RunId

class DatabaseRunContext(
    private val databaseConfig: DatabaseConfig
) : RunContext {

    override fun currentRunId(): RunId =
        databaseConfig.getCurrentRunId()
}