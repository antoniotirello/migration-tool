package io.github.antoniotirello.migrationtool.logs

import kotlinx.serialization.Serializable

@Serializable
enum class LogEventType {
    RUN_START,
    RUN_COMPLETED,
}