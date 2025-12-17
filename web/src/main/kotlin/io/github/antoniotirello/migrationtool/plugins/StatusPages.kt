package io.github.antoniotirello.migrationtool.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        // Eccezioni non gestite
        exception<Throwable> { call, cause ->
            // sempre utile loggare
            call.application.environment.log.error("Unhandled exception", cause)

            // provare a rispondere, ma non dare per scontato che arrivi al client
            try {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to (cause.message ?: "Unknown error"))
                )
            } catch (_: Exception) {
                // pipeline già fallita: al massimo loggare
                println("Impossibile inviare la risposta al client, eccezione: ${cause.message}")
            }
        }

        // 404
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "Not Found"))
        }
    }
}