package io.github.antoniotirello.migrationtool.infrastructure.logging.api

import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.RunId

interface RunContext {
    fun currentRunId(): RunId
}