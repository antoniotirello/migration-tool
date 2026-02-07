package io.github.antoniotirello.migrationtool.context

import io.github.antoniotirello.migrationtool.logging.dao.Run
import io.github.antoniotirello.migrationtool.logging.dao.RunId
import io.github.antoniotirello.migrationtool.logging.database.SchemaManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.LocalDateTime

object AppBootstrap {
    fun init(context: AppContext): AppContext {
        SchemaManager().createAll(context.database)

        val run = transaction(context.database) {
            Run.new {
                startedAt = LocalDateTime.now()
                appVersion = context.appVersion
            }
        }

        val runId = run.id.value


        return AppContext(
            appVersion = context.appVersion,
            database = context.database,
            currentRunId = RunId(runId),
        )
    }
}