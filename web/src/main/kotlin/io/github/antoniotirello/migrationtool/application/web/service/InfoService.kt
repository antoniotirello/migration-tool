package io.github.antoniotirello.migrationtool.application.web.service

import io.github.antoniotirello.migrationtool.MigrationToolInfo
import io.github.antoniotirello.migrationtool.application.web.dto.InfoDto
import io.github.antoniotirello.migrationtool.context.AppContext
import org.springframework.stereotype.Service

@Service
class InfoService(
    private val context: AppContext,
) {

    fun getInfo(): InfoDto =
        InfoDto(
            version = MigrationToolInfo.VERSION,
            runId = context.currentRunId?.value
        )
}