package io.github.antoniotirello.migrationtool.application.web.controller

import io.github.antoniotirello.migrationtool.application.web.dto.ApiPaths
import org.springframework.boot.actuate.context.ShutdownEndpoint
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Actuator", description = "Endpoints di gestione del server")
@RequestMapping(ApiPaths.V1 + "/shutdown")
@RestController
class ShutdownController(
    private val shutdownEndpoint: ShutdownEndpoint
) {

    @Operation(summary = "Chiude il server Spring Boot")
    @ApiResponses(
        ApiResponse(responseCode = "202", description = "Server shutting down...")
    )
    @PostMapping
    fun shutdown(): ResponseEntity<String> {
        Thread {
            shutdownEndpoint.shutdown()
        }.start()

        return ResponseEntity.accepted().body("Server shutting down...")
    }
}