package io.github.antoniotirello.migrationtool.logging.dao

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object LogEntries : LongIdTable("log_entries") {
    val runId = reference("run_id", Runs, onDelete = ReferenceOption.CASCADE)
    val timestamp = datetime("timestamp").clientDefault { LocalDateTime.now() }
    val level = integer("level")
    val message = text("message")
    val payload = text("payload").nullable() // JSON opzionale per dati extra
    val event = text("event").nullable()
    val exceptionType = text("exception_type").nullable()
    val exceptionMessage = text("exception_message").nullable()
    val stacktrace = text("stacktrace").nullable()
}

class LogEntry(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LogEntry>(LogEntries)

    var runId by Run.Companion referencedOn LogEntries.runId
    var timestamp by LogEntries.timestamp
    var level by LogEntries.level
    var message by LogEntries.message
    var payload by LogEntries.payload
    var event by LogEntries.event
    var exceptionType by LogEntries.exceptionType
    var exceptionMessage by LogEntries.exceptionMessage
    var stacktrace by LogEntries.stacktrace
}