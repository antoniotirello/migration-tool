package io.github.antoniotirello.migrationtool.api

enum class MigrationLanguage {
    JAVA,
    KOTLIN;

    companion object {
        fun from(value: String): MigrationLanguage =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: error(
                    "Invalid migrationTool.language '$value'. " +
                            "Allowed values: ${entries.joinToString { it.name }}"
                )
    }
}