package io.github.antoniotirello.migrationtool.application.web.controller

import io.github.antoniotirello.migrationtool.application.web.request.PageRequest
import io.github.antoniotirello.migrationtool.application.web.dto.ApiPaths
import io.github.antoniotirello.migrationtool.application.web.service.LogReaderService
import io.github.antoniotirello.migrationtool.application.web.sse.SseManager
import io.github.antoniotirello.migrationtool.dto.PagedResponse
import io.github.antoniotirello.migrationtool.logging.dto.LogEntryEvent
import io.github.antoniotirello.migrationtool.logging.dto.RunsResults
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Tag(name = "Log", description = "Get server logs")
@RequestMapping(ApiPaths.V1 + "/log")
@RestController
class LogController(
    private val logReaderService: LogReaderService,
    private val sseManager: SseManager
) {
    @GetMapping
    @Operation(
        summary = "Get all migration runs",
        description = """
Returns the list of all migration runs executed on the server.

The current run is identified by `lastRun` equal to **true**. 
    """
    )
    fun getAllRuns(
        @Valid pageRequest: PageRequest? = null
    ): PagedResponse<RunsResults> {
        val page = pageRequest?.page ?: 0
        val size = pageRequest?.size ?: 10
        return logReaderService.getAllRuns(page, size)
    }

    @GetMapping("/events", produces = ["text/event-stream"])
    @Operation(
        summary = "Stream live logs for a run",
        description = """
Opens a Server-Sent Events stream.

The connection remains open while the run is in RUNNING state.
The server pushes log events in real time.<br/>
<br/><br/>
**Important:** It is strongly recommended **not to use the *'Try it out'* button** for this endpoint, 
because SSE connections remain open and may not behave correctly in Swagger UI.<br> 
Instead, use the dedicated live viewer page [HERE](/logs.html).
"""
    )
    @ApiResponse(
        responseCode = "200",
        description = "Event stream of log entries",
        content = [Content(mediaType = "text/event-stream")]
    )
    fun streamRunLogs(): SseEmitter {
        return sseManager.register()
    }

    @GetMapping("/details", produces = ["application/json"])
    @Operation(
        summary = "Get details of a run",
        description = """
If the `runId` param is not set, last run will be used.
"""
    )
    fun streamRunLogs(@RequestParam(required = false) runId: Long?): List<LogEntryEvent> {
        return logReaderService.getRunDetail(runId)
    }
}