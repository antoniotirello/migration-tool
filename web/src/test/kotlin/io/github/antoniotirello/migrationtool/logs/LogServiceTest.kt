package io.github.antoniotirello.migrationtool.logs

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.writeText
import kotlin.test.Test

class LogServiceTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `append should create the log file if it doesn't exist`() {
        val logFile = tempDir.resolve("run-test.jsonl")

        val logService = LogService(
            logDirectory = tempDir,
            logFile = logFile,
            runId = "test"
        )

        assertFalse(logFile.exists(), "File should not exist before append")

        logService.append("Hello world")

        assertTrue(logFile.exists(), "File should exist after append")
    }

    @Test
    fun `append should record the log correctly`() {
        val logFile = tempDir.resolve("run-test.jsonl")

        val runId = "myTestRun"
        val logWithDefaultValues = "Just the message"
        val logWithCustomValues = "All values here"

        val logService = LogService(
            logDirectory = tempDir,
            logFile = logFile,
            runId = runId
        )

        logService.append(
            message = logWithDefaultValues,
        )
        logService.append(
            message = logWithCustomValues,
            level = LogLevel.ERROR,
            eventType = LogEventType.RUN_COMPLETED
        )

        val json = Json { ignoreUnknownKeys = true }

        val records: List<LogRecord> = logFile.toFile().readLines().map { line ->
            json.decodeFromString<LogRecord>(line)
        }

        val defaultLog = records.find { it.message == logWithDefaultValues }!!
        assertEquals(runId, defaultLog.runId)
        assertEquals(LogLevel.INFO, defaultLog.level)
        assertEquals(null, defaultLog.event)

        val customLog = records.find { it.message == logWithCustomValues }!!
        assertEquals(runId, customLog.runId)
        assertEquals(LogLevel.ERROR, customLog.level)
        assertEquals(LogEventType.RUN_COMPLETED, customLog.event)

        assertEquals(records.size, 2, "Total number of records")
    }

    fun setupIncompleteRunLogs(lastRunId: String): Path {
        val logFile1 = tempDir.resolve("run-test-1.jsonl")

        logFile1.writeText(
            """
            {"timestamp":1767096018828,"runId":"${lastRunId}xx","event":"RUN_START","level":"INFO","message":"Started WebApp"}
            """.trimIndent()
        )

        val toBeExcludedFile = tempDir.resolve("run-test-error.xxx")

        toBeExcludedFile.writeText(
            """
            xxx
            """.trimIndent()
        )

        val logFile2 = tempDir.resolve("run-$lastRunId.jsonl")

        logFile2.writeText(
            """
            {"timestamp":1767096018828,"runId":"$lastRunId","event":"RUN_START","level":"INFO","message":"Started WebApp"}
            """.trimIndent()
        )

        return logFile2
    }

    @Test
    fun `readAll should list all jsonl files and exclude others types`() {
        val lastRunId = "asd"
        val currentRunLog = setupIncompleteRunLogs(lastRunId)

        val logService = LogService(
            logDirectory = tempDir,
            logFile = currentRunLog,
            runId = lastRunId
        )

        val records = logService.readAll()

        assertEquals(records.size, 2, "Total number of records")
    }

    @Test
    fun `getLogSummary should return the correct filename as fileName`() {
        val lastRunId = "asd"
        val currentRunLog = tempDir.resolve("run-test-1.jsonl")

        currentRunLog.writeText(
            """
            {"timestamp":1767096018828,"runId":"${lastRunId}xx","event":"RUN_START","level":"INFO","message":"Started WebApp"}
            """.trimIndent()
        )

        val logService = LogService(
            logDirectory = tempDir,
            logFile = currentRunLog,
            runId = lastRunId
        )

        val dto = logService.getLogSummary(currentRunLog)

        assertEquals(currentRunLog.name, dto.fileName, "file name incorrect")
    }

    @Test
    fun `getLogSummary should return logEntries as counter of entries`() {
        val lastRunId = "asd"
        val currentRunLog = tempDir.resolve("run-test-1.jsonl")

        currentRunLog.writeText(
            """
            {"timestamp":1767096018828,"runId":"${lastRunId}xx","event":"RUN_START","level":"INFO","message":"Started WebApp"}
            {"timestamp":1767096018828,"runId":"${lastRunId}xx","event":"RUN_START","level":"INFO","message":"Started WebApp"}
            {"timestamp":1767096018828,"runId":"${lastRunId}xx","event":"RUN_START","level":"INFO","message":"Started WebApp"}
            """.trimIndent()
        )

        val logService = LogService(
            logDirectory = tempDir,
            logFile = currentRunLog,
            runId = lastRunId
        )

        val dto = logService.getLogSummary(currentRunLog)

        assertEquals(3, dto.logEntries, "logEntries wrong")
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("logIsCurrentLines")
    fun `getLogSummary should compute correctly isCurrent`(
        description: String,
        logContent: String,
        runId: String,
        expected: Boolean
    ) {
        val logFile = tempDir.resolve("test.jsonl")
        logFile.writeText(logContent.trimIndent())

        val logService = LogService(
            logDirectory = tempDir,
            logFile = logFile,
            runId = runId
        )

        val dto = logService.getLogSummary(logFile)

        assertEquals(expected, dto.isCurrent, description)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("logSuccessLines")
    fun `getLogSummary should compute correctly isSuccess`(
        description: String,
        logContent: String,
        expected: Boolean
    ) {
        val logFile = tempDir.resolve("test.jsonl")
        logFile.writeText(logContent.trimIndent())

        val logService = LogService(
            logDirectory = tempDir,
            logFile = logFile,
            runId = "2025-12-30T12-00-18"
        )

        val dto = logService.getLogSummary(logFile)

        assertEquals(expected, dto.isSuccess, description)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("logSkippedLines")
    fun `getLogSummary should compute correctly skippedLines without errors`(
        description: String,
        logContent: String,
        expected: Int
    ) {
        val logFile = tempDir.resolve("test.jsonl")
        logFile.writeText(logContent.trimIndent())

        val logService = LogService(
            logDirectory = tempDir,
            logFile = logFile,
            runId = "2025-12-30T12-00-18"
        )

        val dto = logService.getLogSummary(logFile)

        assertEquals(expected, dto.skippedLines, description)
    }

    companion object {

        @JvmStatic
        fun logIsCurrentLines() = listOf(
            Arguments.of(
                "runId same",
                """
                    {"timestamp":1767096018828,"runId":"aaaa","event":"RUN_START","level":"INFO","message":""}
                    {"timestamp":1767096018828,"runId":"aaaa","event":"RUN_COMPLETED","level":"INFO","message":"Completed ok"}
                """.trimIndent(),
                "aaaa",
                true,
            ),
            Arguments.of(
                "runId different but similar",
                """
                    {"timestamp":1767096018828,"runId":"aaaa","event":"RUN_START","level":"INFO","message":""}
                    {"timestamp":1767096018828,"runId":"aaaa","event":"RUN_COMPLETED","level":"INFO","message":"Completed ok"}
                """.trimIndent(),
                "aaa",
                false,
            ),
            Arguments.of(
                "runId different",
                """
                    {"timestamp":1767096018828,"runId":"aaaa","event":"RUN_START","level":"INFO","message":""}
                    {"timestamp":1767096018828,"runId":"aaaa","event":"RUN_COMPLETED","level":"INFO","message":"Completed ok"}
                """.trimIndent(),
                "bbbb",
                false,
            ),
            Arguments.of(
                "runId inconsistent: at least one entry matches current run - last log candidate",
                """
                    {"timestamp":1767096018828,"runId":"aaaa","event":"RUN_START","level":"INFO","message":""}
                    {"timestamp":1767096018828,"runId":"bbb","event":"RUN_COMPLETED","level":"INFO","message":"Completed ok"}
                """.trimIndent(),
                "aaaa",
                true,
            ),
        )

        @JvmStatic
        fun logSuccessLines() = listOf(
            Arguments.of(
                "RUN_COMPLETED ok",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":""}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_COMPLETED","level":"INFO","message":"Completed ok"}
                """.trimIndent(),
                true,
            ),
            Arguments.of(
                "RUN_COMPLETED twice",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":""}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_COMPLETED","level":"INFO","message":"Completed twice"}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_COMPLETED","level":"INFO","message":"Completed twice"}
                """.trimIndent(),
                true,
            ),
            Arguments.of(
                "no RUN_COMPLETED",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"no RUN_COMPLETED"}
                """.trimIndent(),
                false,
            ),
        )

        @JvmStatic
        fun logSkippedLines() = listOf(
            Arguments.of(
                "No skipped lines",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp","notExists":true}
                """.trimIndent(),
                0,
            ),
            Arguments.of(
                "Missing timestamp",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                    {"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                """.trimIndent(),
                1,
            ),
            Arguments.of(
                "Missing runId",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                    {"timestamp":1767096018828,"event":"RUN_START","level":"INFO","message":"Started WebApp"}
                """.trimIndent(),
                1,
            ),
            Arguments.of(
                "Missing event: it's optional",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","level":"INFO","message":"Started WebApp"}
                """.trimIndent(),
                0,
            ),
            Arguments.of(
                "Missing level",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","message":"Started WebApp"}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","message":"Started WebApp"}
                """.trimIndent(),
                2,
            ),
            Arguments.of(
                "Missing message",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO"}
                """.trimIndent(),
                1,
            ),
            Arguments.of(
                "Truncated log",
                """
                    {"timestamp":1767096018828,"runId":"2025-12-30T12-00-18","event":"RUN_START","level":"INFO","message":"Started WebApp"}
                    {"timestamp":1,"runId":"run","level":"INFO","message":"oops"
                """.trimIndent(),
                1,
            ),
        )
    }
}
