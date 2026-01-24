package io.github.antoniotirello.migrationtool.application.web.controller

import io.github.antoniotirello.migrationtool.application.web.dto.ApiPaths
import io.github.antoniotirello.migrationtool.application.web.dto.InfoDto
import io.github.antoniotirello.migrationtool.application.web.service.InfoService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Info", description = "Get server info")
@RequestMapping(ApiPaths.V1 + "/info")
@RestController
class InfoController (
    private val infoService: InfoService
) {

    @GetMapping
    fun info(): InfoDto {
        return infoService.getInfo()
    }
}