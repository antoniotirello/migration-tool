package io.github.antoniotirello.migrationtool.application.web.controller

import io.github.antoniotirello.migrationtool.application.web.dto.ApiPaths
import io.github.antoniotirello.migrationtool.application.web.service.LogReaderService
import io.github.antoniotirello.migrationtool.infrastructure.logging.database.RunsResults
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Log", description = "Get server logs")
@RequestMapping(ApiPaths.V1 + "/log")
@RestController
class LogController(
    private val logReaderService: LogReaderService
) {
    @GetMapping
    @Operation(
        summary = "Get all migration runs",
        description = """
Returns the list of all migration runs executed on the server.

The current run is identified by `lastRun` equal to **true**. 
    """
    )
    fun getAllRuns(): List<RunsResults> {
        return logReaderService.getAllRuns()
    }
}