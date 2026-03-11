package io.github.antoniotirello.migrationtool.logging.database

import io.github.antoniotirello.migrationtool.logging.dao.LogEntries
import io.github.antoniotirello.migrationtool.logging.dao.Runs
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SchemaManager {
    fun createAll(db: Database) = transaction(db) {
        SchemaUtils.create(
            Runs,
            LogEntries,
        )
    }

    fun dropAll(db: Database) = transaction(db) {
        SchemaUtils.drop(
            Runs,
            LogEntries
        )
    }
}