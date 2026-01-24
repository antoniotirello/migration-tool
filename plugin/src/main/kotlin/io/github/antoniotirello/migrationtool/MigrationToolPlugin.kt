package io.github.antoniotirello.migrationtool.plugin

import io.github.antoniotirello.migrationtool.LaunchMigrationToolTask
import io.github.antoniotirello.migrationtool.MigrationToolExtension
import io.github.antoniotirello.migrationtool.MigrationToolInfo
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Implementation class of the `migrationToolPlugin`
 */
@Suppress("unused")
class MigrationToolPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "migrationTool",
            MigrationToolExtension::class.java
        )

        // Crea una configuration per le dipendenze del launcher
        val launcherConfig = project.configurations.create("migrationToolLauncher") {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        /*
            MigrationToolInfo is generated at build time under the /build folder.
            It won't exist immediately after `clean`, so make sure to run a build first.
        */
        val toolVersion = MigrationToolInfo.VERSION

        project.dependencies.add(
            "migrationToolLauncher",
            "io.github.antoniotirello.migrationtool:migration-tool-launcher:${toolVersion}"
        )

        val webServerConfig = project.configurations.create("migrationToolWebServer") {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        project.dependencies.add(
            "migrationToolWebServer",
            mapOf(
                "group" to "io.github.antoniotirello.migrationtool",
                "name" to "web",
                "version" to toolVersion,
                "classifier" to "boot"
            )
        )

        project.tasks.register("launchMigrationTool", LaunchMigrationToolTask::class.java) {
            group = "migration"
            description = "Launches the migration tool in a separate process"
            launcherClasspath.from(launcherConfig)

            migrationsSourceDir.convention(extension.sourceDir)
            migrationsPackageName.convention(extension.packageName)
            migrationsLanguage.convention(extension.language)

            migrationToolVersion.set(toolVersion)

            configFile.set(
                project.layout.buildDirectory.file("migration-tool/config.json")
            )

            // Qui passiamo il boot jar come RegularFileProperty
            webServerJar.set(
                project.layout.file(
                    project.provider {
                        val files = webServerConfig.incoming.artifactView {}.files
                        files.single() // restituisce un File
                    }
                )
            )
        }
    }
}