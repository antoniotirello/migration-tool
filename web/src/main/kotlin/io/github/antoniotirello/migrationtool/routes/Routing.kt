package io.github.antoniotirello.migrationtool.routes

import io.github.antoniotirello.migrationtool.responses.InfoResponse
import io.github.antoniotirello.migrationtool.services.InfoService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(onStop: () -> Unit) {
    routing {
        get("/") {
            call.respondRedirect("/swagger")
        }

        get("/info") {
            val info = InfoService.getBackendInfo()
            call.respond(InfoResponse.fromModel(info))
        }

        post("/stop") {
            call.respond(HttpStatusCode.Accepted, "Stopping server, please wait...")
            onStop()
        }

        swaggerUI(
            path = "swagger",
            swaggerFile = "openapi/documentation.yaml"
        )
    }
}
