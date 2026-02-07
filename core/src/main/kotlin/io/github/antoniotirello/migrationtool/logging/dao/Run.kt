package io.github.antoniotirello.migrationtool.logging.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object Runs : LongIdTable("runs") {
    val startedAt = datetime("started_at").clientDefault { LocalDateTime.now() }
    var endedAt = datetime("ended_at").nullable()

    var appVersion = text("app_version").nullable()
    var environment = text("environment").nullable()
    var triggeredBy = text("triggered_by").nullable()
}

// Entity DAO
class Run(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Run>(Runs)

    var startedAt by Runs.startedAt
    var endedAt by Runs.endedAt
    val logs by LogEntry referrersOn LogEntries.runId

    var appVersion by Runs.appVersion
    var environment by Runs.environment
    var triggeredBy by Runs.triggeredBy
}