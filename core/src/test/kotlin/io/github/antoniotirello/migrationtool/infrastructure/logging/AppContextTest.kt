package io.github.antoniotirello.migrationtool.infrastructure.logging

import io.github.antoniotirello.migrationtool.context.AppContextBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AppContextTest {

    @Test
    fun `should throw when configuring database twice`() {
        val builder = AppContextBuilder().useH2InMemory()

        val exception = assertThrows<IllegalStateException> {
            builder.useSqliteFile("foo.db")
        }

        assert(exception.message == "Database already configured")
    }

    @Test
    fun `should throw when building without database`() {
        val builder = AppContextBuilder().withVersion("1.0")

        val exception = assertThrows<IllegalStateException> {
            builder.build()
        }

        assert(exception.message == "Database not configured")
    }

    @Test
    fun `context should be created correctly with version`() {
        val version = "0.69.42"
        val context = AppContextBuilder()
            .withVersion(version)
            .useH2InMemory()
            .build()

        assertEquals(version, context.appVersion)
        assertNotNull(context.database)
    }
}