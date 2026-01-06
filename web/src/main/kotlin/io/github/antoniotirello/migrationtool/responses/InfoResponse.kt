package io.github.antoniotirello.migrationtool.responses

import io.github.antoniotirello.migrationtool.models.BackendInfo
import kotlinx.serialization.Serializable

@Serializable
data class InfoResponse(
    val version: String,
    val name: String,
    val rootPath: String,
) {
    companion object {
        fun fromModel(info: BackendInfo): InfoResponse =
            InfoResponse(
                version = info.version,
                name = info.name,
                rootPath = info.projectAbsolutePath,
            )
    }
}