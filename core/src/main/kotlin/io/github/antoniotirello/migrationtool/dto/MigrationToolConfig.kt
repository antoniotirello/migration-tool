package io.github.antoniotirello.migrationtool.dto

data class MigrationToolConfig(
    val migrationsPackage: String,
    val webServerJar: String,
    val projectDir: String,
    val projectClasspath: String,
    var toolVersion: String,
)
