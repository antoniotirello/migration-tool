package io.github.antoniotirello.migrationtool.launcher

import io.github.antoniotirello.migrationtool.dto.MigrationToolConfig
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.awt.Desktop
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.ServerSocket
import java.net.URI
import java.time.Duration
import java.time.Instant

fun main(args: Array<String>) {
    println("Hello from Launcher!")
    println("Args: ${args.joinToString(", ")}")
    println("Working directory: ${System.getProperty("user.dir")}")

    val configPath = args
        .firstOrNull { it.startsWith("--config=") }
        ?.substringAfter("=")
        ?: error("Missing --config argument")

    val configFile = File(configPath)
    require(configFile.exists()) {
        "Config file not found: $configPath"
    }

    val mapper = jacksonObjectMapper().findAndRegisterModules()

    val config = mapper.readValue<MigrationToolConfig>(
        File(configPath)
    )

    println("Loaded config: $config")

    val webJar = File(config.webServerJar)

    if (!webJar.exists()) {
        System.err.println("Web JAR not found: ${config.webServerJar}")
        exitProcess(1)
    }

    if (!webJar.isFile) {
        System.err.println("Web JAR path is not a file: ${config.webServerJar}")
        exitProcess(1)
    }

    if (!webJar.name.endsWith(".jar", ignoreCase = true)) {
        System.err.println("Web JAR does not have a .jar extension: ${webJar.name}")
        exitProcess(1)
    }

    println("Web JAR file exists and looks like a JAR: ${webJar.absolutePath}")

    val javaBin = Paths.get(
        System.getProperty("java.home"),
        "bin",
        "java"
    ).toAbsolutePath().toString()

    val finalClasspath = buildString {
        append(webJar.absolutePath)
        append(File.pathSeparator)
        append(config.projectClasspath)
    }

    val port = ServerSocket(0).use { it.localPort }

    val command = listOf(
        javaBin,
        "-cp",
        finalClasspath,
        "org.springframework.boot.loader.launch.JarLauncher",
        "--server.port=$port"
    )

    val logFile = File(config.projectDir).resolve("migration-tool.log")
    logFile.parentFile.mkdirs()

    val webServer = ProcessBuilder(command)
        .inheritIO()

    webServer.environment()["BACKEND_CONFIG_PROJECT_PATH"] = config.projectDir

    val process = webServer.start()

    if (config.openBrowser) {
        println("⏳ Waiting server...")

        if (waitForServer("http://localhost:$port/actuator/health", Duration.ofSeconds(30))) {
            openBrowser("http://localhost:$port")
        }
    }

    println("Server started! Pid: ${process.pid()}")

    println("Launcher finished!")
}

fun openBrowser(url: String) {
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
        println("🦊 Firefox aperto")
    } else {
        println("⚠ Firefox non trovato, uso il browser di default")
        Desktop.getDesktop().browse(URI(url))
    }
}

fun isWindows(): Boolean =
    System.getProperty("os.name").lowercase().contains("win")

fun isMac(): Boolean =
    System.getProperty("os.name").lowercase().contains("mac")

fun waitForServer(
    url: String,
    timeout: Duration = Duration.ofSeconds(30), // Aumentato timeout
    pollIntervalMs: Long = 500 // Poll più frequente
): Boolean {
    val deadline = Instant.now().plus(timeout)

    while (Instant.now().isBefore(deadline)) {
        try {
            val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 1000
            connection.readTimeout = 1000

            val responseCode = connection.responseCode
            println("⏳ Server response: $responseCode")

            if (responseCode in 200..299) {
                println("✅ Server pronto!")
                return true
            }
        } catch (_: Exception) {
            // server not ready yet - silenzioso
        }

        sleep(pollIntervalMs)
    }

    return false
}