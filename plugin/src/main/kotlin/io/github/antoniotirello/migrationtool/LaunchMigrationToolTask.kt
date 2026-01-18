
package io.github.antoniotirello.migrationtool.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import java.io.File

abstract class LaunchMigrationToolTask : DefaultTask() {

    @get:InputFile
    abstract val webServerJar: RegularFileProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val launcherClasspath: ConfigurableFileCollection

    @get:Input
    abstract val javaExecutable: ListProperty<String>

    @get:Input
    abstract val mainClass: ListProperty<String>

    init {
        outputs.upToDateWhen { false }

        javaExecutable.convention(
            project.providers.provider {
                val javaHome = System.getProperty("java.home")
                listOf(File(javaHome, "bin/java").absolutePath)
            }
        )

        mainClass.convention(
            listOf("io.github.antoniotirello.migrationtool.launcher.MainKt")
        )
    }

    @TaskAction
    fun launch() {

        val webJarFile = webServerJar.get().asFile
        check(webJarFile.exists()) { "Web server jar not found: ${webJarFile.absolutePath}" }
        logger.lifecycle("Web server jar found: ${webJarFile.absolutePath}")

        val classpath = launcherClasspath.files.joinToString(File.pathSeparator)
        val javaBin = javaExecutable.get()[0]
        val main = mainClass.get()[0]

        val projectClasspath = project.files(
            project.layout.buildDirectory.dir("classes/kotlin/main"),
            project.layout.buildDirectory.dir("classes/java/main"),
            project.layout.buildDirectory.dir("resources/main")
        )

        val projectClasspathArg =
            projectClasspath.files.joinToString(File.pathSeparator) { it.absolutePath }

        val command = listOf(
            javaBin,
            "-cp",
            classpath,
            main,
            webServerJar.get().asFile.absolutePath,
            project.projectDir.absolutePath,
            projectClasspathArg
        )

        logger.lifecycle("Launching in separate process...")
        logger.lifecycle("Command: ${command.joinToString(" ")}")

        val logFile = project.layout.buildDirectory
            .file("migration-tool.log")
            .get()
            .asFile

        val processBuilder = ProcessBuilder(command)
            .directory(project.projectDir)
            .redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))
            .redirectError(ProcessBuilder.Redirect.appendTo(logFile))

        val process = processBuilder.start()

        logger.lifecycle("Process started (PID: ${process.pid()})")
        logger.lifecycle("Gradle will exit immediately, process continues in background")

        logger.lifecycle("Process is running successfully!")
    }
}