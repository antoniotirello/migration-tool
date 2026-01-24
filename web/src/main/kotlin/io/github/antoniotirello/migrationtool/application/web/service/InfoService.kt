package io.github.antoniotirello.migrationtool.application.web.service

import io.github.antoniotirello.migrationtool.MigrationToolInfo
import io.github.antoniotirello.migrationtool.application.web.dto.InfoDto
import org.springframework.stereotype.Service

@Service
class InfoService {

    fun getInfo(): InfoDto =
        InfoDto(
            version = MigrationToolInfo.VERSION
        )
}