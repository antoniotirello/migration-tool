package io.github.antoniotirello.migrationtool

import io.github.antoniotirello.migrationtool.plugins.configureSerialization
import io.github.antoniotirello.migrationtool.plugins.configureStatusPages
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.github.antoniotirello.migrationtool.routes.configureRouting

object WebApp {
    fun run(port: Int, onStop: (KtorWebServerHandle) -> Unit): KtorWebServerHandle {
        lateinit var handle: KtorWebServerHandle

        val server = embeddedServer(Netty, port = port) {
            configureRouting {
                onStop(handle)
            }
            configureSerialization()
            configureStatusPages()
        }
        server.start(wait = false)

        handle = KtorWebServerHandle(server.engine)
        return handle
    }

    fun getRandomFreePort(): Int =
        java.net.ServerSocket(0).use { it.localPort }
}