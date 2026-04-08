package io.github.antoniotirello.migrationtool.application

import io.github.antoniotirello.migrationtool.application.web.config.TestDatabaseConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.Test

@SpringBootTest
@Import(TestDatabaseConfig::class)
class MainTest {
    @Test
    fun `context loads`() {
        // Verifies that the Spring context starts without errors.
        // No assertions needed: a startup exception would automatically fail the test.
    }
}