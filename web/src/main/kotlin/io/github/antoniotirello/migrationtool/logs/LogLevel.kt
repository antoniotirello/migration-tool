package io.github.antoniotirello.migrationtool.logs

import kotlinx.serialization.Serializable

@Serializable
enum class LogLevel() {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}