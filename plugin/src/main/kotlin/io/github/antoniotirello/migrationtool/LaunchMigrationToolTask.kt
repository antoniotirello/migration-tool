package io.github.antoniotirello.migrationtool

import io.github.antoniotirello.migrationtool.dto.MigrationToolConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.antoniotirello.migrationtool.api.MigrationLanguage

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

    @get:Input
    @get:Optional
    abstract val migrationsSourceDir: Property<String>

    @get:Input
    @get:Optional
    abstract val migrationsPackageName: Property<String>

    @get:Input
    @get:Optional
    abstract val migrationsLanguage: Property<MigrationLanguage>

    @get:OutputFile
    abstract val configFile: RegularFileProperty

    @get:Input
    abstract val migrationToolVersion: Property<String>

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
        val errors = mutableListOf<String>()

        val sourceDir = migrationsSourceDir.orNull
            ?.takeIf { it.isNotBlank() }
            ?: run {
                errors.add("migrationTool.sourceDir is required (relative to project root)")
                null
            }

        val packageName = migrationsPackageName.orNull
            ?.takeIf { it.isNotBlank() }
            ?: run {
                errors.add("migrationTool.packageName is required and cannot be blank")
                null
            }

        val language = migrationsLanguage.orNull
            ?: run {
                errors.add("migrationTool.language is required (JAVA or KOTLIN)")
                null
            }

        val message = buildString {
            appendLine("Migration tool configuration is invalid:")
            appendLine()
            errors.forEach { appendLine("  • $it") }
            appendLine()
            appendLine("Example:")
            appendLine()
            appendLine("migrationTool {")
            appendLine("    sourceDir = \"src/main/kotlin/io/github/my_company/my_project/my_modules/mig\"")
            appendLine("    packageName = \"io.github.my_company.my_project.mig\"")
            appendLine("    language = JAVA")
            appendLine("}")
        }

        if (errors.isNotEmpty()) {
            error(message)
        }

        println("Migrations package = $packageName")

        val configFile = configFile.get().asFile

        configFile.parentFile.mkdirs()

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

        val mapper = jacksonObjectMapper().findAndRegisterModules()

        configFile.writeText(
            mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                    MigrationToolConfig(
                        migrationsPackage = packageName.toString(),
                        webServerJar = webServerJar.get().asFile.absolutePath,
                        projectDir = project.projectDir.absolutePath,
                        projectClasspath = projectClasspathArg,
                        toolVersion = migrationToolVersion.get()
                    )
                )
        )

        val command = listOf(
            javaBin,
            "-cp",
            classpath,
            main,
            "--config=${configFile.absolutePath}"
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