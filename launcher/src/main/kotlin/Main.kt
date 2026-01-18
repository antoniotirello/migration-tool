package io.github.antoniotirello.migrationtool.launcher

import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("Hello from Launcher!")
    println("Args: ${args.joinToString(", ")}")
    println("Working directory: ${System.getProperty("user.dir")}")

    if (args.isEmpty()) {
        System.err.println("Usage: launcher <web-jar-path> [project-dir]")
        exitProcess(1)
    }

    val webJarPath = args[0]
    //val projectDir = if (args.size > 1) args[1] else System.getProperty("user.dir")

    val webJar = File(webJarPath)

    // Controlli di base sul file
    if (!webJar.exists()) {
        System.err.println("Web JAR not found: $webJarPath")
        exitProcess(1)
    }

    if (!webJar.isFile) {
        System.err.println("Web JAR path is not a file: $webJarPath")
        exitProcess(1)
    }

    if (!webJar.name.endsWith(".jar", ignoreCase = true)) {
        System.err.println("Web JAR does not have a .jar extension: ${webJar.name}")
        exitProcess(1)
    }

    println("Web JAR file exists and looks like a JAR: ${webJar.absolutePath}")

    val projectClasspath = args[2]

    val javaBin = Paths.get(
        System.getProperty("java.home"),
        "bin",
        "java"
    ).toAbsolutePath().toString()

    val finalClasspath = buildString {
        append(webJar.absolutePath)
        append(File.pathSeparator)
        append(projectClasspath)
    }

    val command = listOf(
        javaBin,
        "-cp",
        finalClasspath,
        "org.springframework.boot.loader.launch.JarLauncher"
    )

    val webServer = ProcessBuilder(command)
        .inheritIO()
        .start()

    println("Server started! Pid: ${webServer.pid()}")

    println("Launcher finished!")
}