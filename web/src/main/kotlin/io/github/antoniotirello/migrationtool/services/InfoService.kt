package io.github.antoniotirello.migrationtool.services

import io.github.antoniotirello.migrationtool.models.BackendInfo
import java.util.Properties

object InfoService {
    fun getBackendInfo(): BackendInfo {
        val props = Properties()

        val stream = Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("META-INF/build-info.properties")
            ?: error("build-info.properties not found in classpath")

        stream.use { props.load(it) }
        return BackendInfo(
            version = props.getProperty("version") ?: "Unknown",
            name = props.getProperty("name") ?: "Unknown"
        )
    }
}