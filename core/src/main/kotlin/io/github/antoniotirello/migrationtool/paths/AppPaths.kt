package io.github.antoniotirello.migrationtool.paths

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

object AppPaths {
    fun homeDir(): Path =
        Paths.get(
            System.getProperty("user.home")
        )

    fun sqliteDbFile(projectPath: String): Path {
        val projectDir = homeDir().resolve(".migrationtool").resolve(projectHash(projectPath))

        Files.createDirectories(projectDir)

        return projectDir.resolve("web-app.db").toAbsolutePath()
    }

    private fun projectHash(projectPath: String): String {
        val normalized = Paths.get(projectPath)
            .toAbsolutePath()
            .normalize()
            .toString()

        val digest = MessageDigest
            .getInstance("SHA-256")
            .digest(normalized.toByteArray(Charsets.UTF_8))

        return digest
            .take(16)
            .joinToString("") { "%02x".format(it) }
    }
}