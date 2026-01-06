package io.github.antoniotirello.migrationtool

import io.github.antoniotirello.migrationtool.logs.LogEventType
import io.github.antoniotirello.migrationtool.logs.LogLevel
import io.github.antoniotirello.migrationtool.logs.LogService
import io.github.antoniotirello.migrationtool.logs.LogServiceWrapper
import io.github.antoniotirello.migrationtool.plugins.configureSerialization
import io.github.antoniotirello.migrationtool.plugins.configureStatusPages
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.github.antoniotirello.migrationtool.routes.configureRouting

object WebApp {
    fun run(
        port: Int,
        config: WebAppConfig
    ): KtorWebServerHandle {
        lateinit var handle: KtorWebServerHandle

        LogServiceWrapper.init(config.projectRoot)

        val server = embeddedServer(Netty, port = port) {
            configureRouting(config) {
                // Chiude il server quando viene chiamato l'endpoint /stop
                handle.stop()

                LogServiceWrapper.get().append(
                    message = "Started WebApp",
                    level = LogLevel.INFO,
                    eventType = LogEventType.RUN_COMPLETED
                )
            }
            configureSerialization()
            configureStatusPages()
        }

        server.start(wait = false) // non blocca il thread chiamante

        handle = KtorWebServerHandle(server.engine)

        LogServiceWrapper.get().append(
            message = "Started WebApp",
            level = LogLevel.INFO,
            eventType = LogEventType.RUN_START
        )

        return handle
    }
}