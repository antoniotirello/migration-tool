package io.github.antoniotirello.migrationtool.infrastructure.logging

import io.github.antoniotirello.migrationtool.context.AppBootstrap
import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.context.AppContextBuilder
import io.github.antoniotirello.migrationtool.logging.database.SchemaManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class TestContextAwareHelper {
    protected lateinit var runtimeContext: AppContext

    @BeforeEach
    fun setup() {
        val appVersion = "69.42"
        val context = AppContextBuilder()
            .useH2InMemory()
            .withVersion(appVersion)
            .build()

        runtimeContext = AppBootstrap.init(context)
    }

    @AfterEach
    fun teardown() {
        SchemaManager().dropAll(runtimeContext.database)
    }
}