package io.github.antoniotirello.migrationtool

import kotlin.concurrent.thread
import kotlin.system.exitProcess


fun main() {

    val port = WebApp.getRandomFreePort()

    WebApp.run(port) { h ->
        thread(start = true) {
            /*
            Calling stop() on a different thread to avoid
            Failed to destroy application instance and Timed out waiting
            */
            println("🛑 stopping server (via endpoint)")
            h.stop()
            exitProcess(0)
        }
    }

    println("🚀 Ktor dev server started on http://localhost:${port}")

    // Put main thread on sleep to avoid that its end will close also the server
    Thread.currentThread().join()
}