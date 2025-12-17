package io.github.antoniotirello.migrationtool

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import kotlin.concurrent.thread
import kotlin.system.exitProcess

abstract class OpenUserInterfaceTask : DefaultTask(){
    @TaskAction
    fun runTool() {
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

        // Put main thread on sleep to avoid that its end will close also the server
        Thread.currentThread().join()
    }
}