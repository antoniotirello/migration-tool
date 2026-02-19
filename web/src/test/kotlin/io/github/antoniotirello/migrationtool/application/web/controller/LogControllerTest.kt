package io.github.antoniotirello.migrationtool.application.web.controller

import io.github.antoniotirello.migrationtool.application.web.config.TestDatabaseConfig
import io.github.antoniotirello.migrationtool.context.AppBootstrap
import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.context.AppContextBuilder
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNull.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestDatabaseConfig::class)
class LogControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    lateinit var runtimeContext: AppContext

    @BeforeEach
    fun setup() {
        val appVersion = "01.01"
        val context = AppContextBuilder()
            .useH2InMemory()
            .withVersion(appVersion)
            .build()

        runtimeContext = AppBootstrap.init(context)
    }

    @Test
    fun `GET logs returns correct JSON`() {
        mockMvc.get("/api/v1/log")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }

                jsonPath("$.content[0].id") { exists() }
                jsonPath("$.content[0].startedAt") { exists() }
                jsonPath("$.content[0].endedAt") { value(nullValue()) }
                jsonPath("$.content[0].environment") { value(nullValue()) }
                jsonPath("$.content[0].lastRun") { value(`is`(true)) }

                jsonPath("$.page") { value(0) }
                jsonPath("$.size") { value(10) }
                jsonPath("$.totalElements") { value(1) }
                jsonPath("$.totalPages") { value(1) }
            }
    }
}