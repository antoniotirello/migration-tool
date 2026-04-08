package io.github.antoniotirello.migrationtool.migration.service

import io.github.antoniotirello.migrationtool.logging.api.LogLevel
import io.github.antoniotirello.migrationtool.logging.api.Logger
import java.io.File

class TemplateService(
    private val logger: Logger,
    private val projectDir: String,
    //private val classLoader: ClassLoader = TemplateService::class.java.classLoader
) {
    fun getTemplate(): String {
        val projectTemplate = File(
            projectDir,
            "src/main/resources/templates/migration_kotlin.tpl"
        )

        val template = if (projectTemplate.exists()) {
            logger.write(LogLevel.INFO, "Project template Found")

            println("Loaded template from PROJECT (filesystem)")
            projectTemplate.readText()
        } else {
            // Load the default template from the tool's classpath.
            // We use TemplateService::class.java to obtain the ClassLoader that loaded this class,
            // ensuring we resolve resources bundled inside this library's JAR.
            //
            // Note: getResourceAsStream looks up resources from the classpath visible to this ClassLoader,
            // not just this JAR. However, by using a namespaced path ("/templates/migrationtool/..."),
            // we minimize the risk of collisions with resources from the consuming project or other dependencies.
            //
            // This is used as a fallback only when no project-level override is found on the filesystem.

            logger.write(LogLevel.INFO, "Loaded template from TOOL (classpath)")
            val stream = TemplateService::class.java
                .getResourceAsStream("/templates/migrationtool/migration_kotlin.tpl")
                ?: error("Template not found anywhere")

            return stream.bufferedReader().readText()
        }

        return template
    }
}