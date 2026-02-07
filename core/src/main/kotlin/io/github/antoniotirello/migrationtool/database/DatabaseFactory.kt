package io.github.antoniotirello.migrationtool.database

import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseFactory {
    fun h2InMemory(name: String = "test"): Database =
        Database.connect(
            url = "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )

    fun sqlite(path: String): Database =
        Database.connect(
            url = "jdbc:sqlite:$path",
            driver = "org.sqlite.JDBC"
        )
}