package io.github.antoniotirello.migrationtool.application.web.controller

import org.springframework.boot.actuate.context.ShutdownEndpoint
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Actuator", description = "Endpoints di gestione del server")
@RestController
class ShutdownController(
    private val shutdownEndpoint: ShutdownEndpoint
) {

    @Operation(summary = "Chiude il server Spring Boot")
    @PostMapping("/api/shutdown")
    fun shutdown(): ResponseEntity<String> {
        Thread {
            shutdownEndpoint.shutdown()
        }.start()

        return ResponseEntity.accepted().body("Server shutting down...")
    }
}