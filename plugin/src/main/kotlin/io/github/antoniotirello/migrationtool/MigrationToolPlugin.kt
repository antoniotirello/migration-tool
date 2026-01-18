package io.github.antoniotirello.migrationtool.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project


class MigrationToolPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Crea una configuration per le dipendenze del launcher
        val launcherConfig = project.configurations.create("migrationToolLauncher") {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        // Aggiungi la dipendenza al launcher
        val versionProvider = project.providers.gradleProperty("migrationToolVersion")
            .orElse("0.0.1")

        project.dependencies.add(
            "migrationToolLauncher",
            "io.github.antoniotirello.migrationtool:migration-tool-launcher:${versionProvider.get()}"
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
                "version" to versionProvider.get(),
                "classifier" to "boot"
            )
        )

        project.tasks.register("launchMigrationTool", LaunchMigrationToolTask::class.java) {
            group = "migration"
            description = "Launches the migration tool in a separate process"
            launcherClasspath.from(launcherConfig)

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