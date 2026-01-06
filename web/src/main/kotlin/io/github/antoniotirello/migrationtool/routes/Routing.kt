package io.github.antoniotirello.migrationtool.routes

import io.github.antoniotirello.migrationtool.WebAppConfig
import io.github.antoniotirello.migrationtool.logs.LogEventType
import io.github.antoniotirello.migrationtool.logs.LogLevel
import io.github.antoniotirello.migrationtool.logs.LogService
import io.github.antoniotirello.migrationtool.logs.LogServiceWrapper
import io.github.antoniotirello.migrationtool.responses.InfoResponse
import io.github.antoniotirello.migrationtool.services.InfoService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Application.configureRouting(config: WebAppConfig, onStop: () -> Unit) {
    routing {
        get("/") {
            call.respondRedirect("/swagger")
        }

        get("/info") {
            val info = InfoService.getBackendInfo(config)

            LogServiceWrapper.get().append(
                message = "Found backend info",
            )

            LogServiceWrapper.get().append(
                message = "Warning",
                level = LogLevel.WARN,
                eventType = LogEventType.RUN_START
            )

            call.respond(InfoResponse.fromModel(info))
        }

        post("/stop") {
            call.respond(HttpStatusCode.Accepted, "Stopping server, please wait...")

            // Stopping the server AFTER response
            launch {
                delay(100) // Ensure that the response is sent
                onStop()
            }
        }

        get("/logs") {
            call.respond(LogServiceWrapper.get().readAll())
        }

        swaggerUI(
            path = "swagger",
            swaggerFile = "openapi/documentation.yaml"
        )
    }
}
