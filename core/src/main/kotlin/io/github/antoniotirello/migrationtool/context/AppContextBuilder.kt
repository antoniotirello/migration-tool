package io.github.antoniotirello.migrationtool.context

import io.github.antoniotirello.migrationtool.database.DatabaseFactory
import org.jetbrains.exposed.v1.jdbc.Database

class AppContextBuilder(
    private var appVersion: String = "0.0.0",
    private var database: Database? = null
) {
    fun withVersion(version: String): AppContextBuilder  = apply {
        appVersion = version
    }

    fun useH2InMemory(name: String = "test_${System.nanoTime()}"): AppContextBuilder = apply {
        if (database != null) error("Database already configured")
        database = DatabaseFactory.h2InMemory(name)
    }

    fun useSqliteFile(path: String): AppContextBuilder = apply {
        if (database != null) error("Database already configured")
        database = DatabaseFactory.sqlite(path)
    }

    fun build(): AppContext =
        AppContext(
            appVersion = appVersion,
            database = database
                ?: error("Database not configured"),
        )
}