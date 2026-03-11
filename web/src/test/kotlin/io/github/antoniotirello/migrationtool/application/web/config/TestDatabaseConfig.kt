package io.github.antoniotirello.migrationtool.application.web.config

import io.github.antoniotirello.migrationtool.context.AppBootstrap
import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.context.AppContextBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestDatabaseConfig {
    @Bean("testDatabaseRunContext")
    @Primary
    fun databaseRunContext(): AppContext {
        val context = AppContextBuilder()
            .useH2InMemory("test_${System.nanoTime()}")
            .withVersion("69.42")
            .build()
        return AppBootstrap.init(context)
    }
}