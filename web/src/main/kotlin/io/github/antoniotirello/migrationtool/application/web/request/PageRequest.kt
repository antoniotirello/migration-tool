package io.github.antoniotirello.migrationtool.application.web.request

import jakarta.validation.constraints.Min

data class PageRequest(
    @field:Min(0)
    val page: Int = 0,
    @field:Min(1)
    val size: Int = 10
)