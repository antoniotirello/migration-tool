package io.github.antoniotirello.migrationtool.migration.service

import io.github.antoniotirello.migrationtool.logging.api.Logger
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TemplateServiceTest {
    @Test
    fun `should fall back to classpath template when project file does not exist`(@TempDir tempDir: File) {
        val logger = mockk<Logger>()

        every {
            logger.write(any(), any(), any())
        } returns Unit

        val service = TemplateService(logger, tempDir.absolutePath)

        assertEquals("JAR TEMPLATE", service.getTemplate())
    }
    @Test
    fun `should load template from project filesystem when file exists`(@TempDir tempDir: File) {
        val templateDir = File(tempDir, "src/main/resources/templates")
        templateDir.mkdirs()
        File(templateDir, "migration_kotlin.tpl").writeText("project template content")

        val logger = mockk<Logger>()

        every {
            logger.write(any(), any(), any())
        } returns Unit

        val service = TemplateService(logger, tempDir.absolutePath)

        assertEquals("project template content", service.getTemplate())
    }
}