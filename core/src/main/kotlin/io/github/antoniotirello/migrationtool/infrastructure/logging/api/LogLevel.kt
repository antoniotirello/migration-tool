package io.github.antoniotirello.migrationtool.infrastructure.logging.api

enum class LogLevel(val severity: Int) {
    FATAL(50),
    ERROR(40),
    WARN(30),
    INFO(20),
    DEBUG(10),
    TRACE(5);

    companion object {
        fun fromSeverity(severity: Int): LogLevel =
            entries.find { it.severity == severity }
                ?: error("Unknown severity: $severity")
    }

    // Check to be able to filter
    fun isAtLeast(other: LogLevel): Boolean = this.severity >= other.severity
}