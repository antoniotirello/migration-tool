package io.github.antoniotirello.migrationtool

import io.ktor.server.engine.ApplicationEngine

class KtorWebServerHandle(
    private val engine: ApplicationEngine
) : WebServerHandle {
    override fun stop() {
        println("🛑 Stopping Ktor server...")
        engine.stop(1000, 2000)
        println("✅ Server stopped")
    }
}