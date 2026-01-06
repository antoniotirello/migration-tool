package io.github.antoniotirello.migrationtool

import io.github.antoniotirello.migrationtool.net.getRandomFreePort
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.Instant
import java.lang.Thread.sleep

@DisableCachingByDefault(because = "Starts an interactive web server")
abstract class OpenUserInterfaceTask : DefaultTask(){
    @get:InputDirectory
    abstract val projectRoot: DirectoryProperty

    @TaskAction
    fun runTool() {
        val port = getRandomFreePort()
        val rootDir: Path = projectRoot.get().asFile.toPath()

        Thread {
            // Config lambda: eventuale stop automatico o logging
            val config = WebAppConfig(
                projectRoot = rootDir,
            )

            WebApp.run(port, config)

            // Il server continua a girare in background
            // Gradle thread principale ritorna subito
        }.start()

        if (project.hasProperty("openBrowser")) {
            // Opening a browser for backend dev
            println("⏳  Waiting for server readiness...")

            if (waitForServer("http://localhost:$port/info")) {
                openBrowser("http://localhost:$port")
            } else {
                println("⚠ Server did not become ready in time")
            }
        }

        //Thread.currentThread().join()
    }

    private fun openBrowser(url: String) {
        val firefoxPaths = when {
            isWindows() -> listOf(
                "C:\\Program Files\\Mozilla Firefox\\firefox.exe",
                "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe"
            )

            isMac() -> listOf(
                "/Applications/Firefox.app/Contents/MacOS/firefox"
            )

            else -> listOf( // Linux
                "/usr/bin/firefox",
                "/snap/bin/firefox",
                "/bin/firefox"
            )
        }

        val firefox = firefoxPaths.firstOrNull { File(it).exists() }

        if (firefox != null) {
            ProcessBuilder(firefox, url).start()
            println("🦊 Firefox opened")
        } else {
            println("⚠ FirefXox not found, falling back to default browser")
            Desktop.getDesktop().browse(URI(url))
        }
    }

    private fun isWindows(): Boolean =
        System.getProperty("os.name").lowercase().contains("win")

    private fun isMac(): Boolean =
        System.getProperty("os.name").lowercase().contains("mac")

    private fun waitForServer(
        url: String,
        timeout: Duration = Duration.ofSeconds(10),
        pollIntervalMs: Long = 300
    ): Boolean {
        val deadline = Instant.now().plus(timeout)

        while (Instant.now().isBefore(deadline)) {
            try {
                val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 500
                connection.readTimeout = 500

                if (connection.responseCode in 200..299) {
                    return true
                }
            } catch (_: Exception) {
                // server not ready yet
            }

            sleep(pollIntervalMs)
        }

        return false
    }
}