package io.github.antoniotirello.migrationtool.infrastructure.logging.database

import io.github.antoniotirello.migrationtool.infrastructure.logging.api.Logger
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.LogEntries
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.Run
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.RunId
import io.github.antoniotirello.migrationtool.infrastructure.logging.entity.Runs
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

object DatabaseConfig {
    private var database: Database? = null
    private var currentRunId: RunId? = null

    /**
     * Initialize sqlite database connection
     * @param dbPath Path of database file (es. "app.db" o "/tmp/app.db")
     */
    fun init(dbPath: String = "app.db", appVersion: String = "0.0.0", triggeredBy: String = "") {
        // Create the directory, if it doesn't exist
        val dbFile = File(dbPath)
        dbFile.parentFile?.mkdirs()

        database = Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        )

        // Create tables
        transaction {
            SchemaUtils.create(
                Runs,
                LogEntries,
            )
        }

        startNewRun(
            appVersion = appVersion,
            triggeredBy = triggeredBy,
        )

        Logger.writer = ExposedLogWriter(DatabaseRunContext(this))
    }

    fun getCurrentRunId(): RunId = currentRunId
        ?: throw IllegalStateException("No run initialized. Call `init()` before!")

    private fun startNewRun(appVersion: String?, triggeredBy: String?): RunId {
        val runId = transaction {
            Run.new {
                this.appVersion = appVersion
                this.triggeredBy = triggeredBy
            }.id.value
        }
        currentRunId = RunId(runId)
        return getCurrentRunId()
    }
}