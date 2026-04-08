package io.github.antoniotirello.migrationtool.application.web.controller

import io.github.antoniotirello.migrationtool.application.web.dto.ApiPaths
import io.github.antoniotirello.migrationtool.application.web.dto.InfoDto
import io.github.antoniotirello.migrationtool.application.web.service.InfoService
import io.github.antoniotirello.migrationtool.logging.api.LogLevel
import io.github.antoniotirello.migrationtool.logging.api.LogWriter
import io.github.antoniotirello.migrationtool.migration.service.TemplateService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Info", description = "Get server info")
@RequestMapping(ApiPaths.V1 + "/system")
@RestController
class SystemInfoController (
    private val infoService: InfoService,
    private val logger: LogWriter,
    private val templateService: TemplateService
) {
    @Value($$"${BACKEND_CONFIG_PROJECT_PATH:/tmp/default}")
    lateinit var projectPath: String

    @GetMapping
    fun info(): InfoDto {
        logger.write(LogLevel.WARN, "Test")
        logger.write(LogLevel.INFO, "Path: $projectPath")

        logger.write(LogLevel.INFO, "Template: ${templateService.getTemplate()}")

        return infoService.getInfo()
    }
}