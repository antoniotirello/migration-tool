package io.github.antoniotirello.migrationtool

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class MigrationToolPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register<OpenUserInterfaceTask>("openMigrationTool") {
            group = "migration"
            description = "Opens the migration tool web interface"
        }
    }
}