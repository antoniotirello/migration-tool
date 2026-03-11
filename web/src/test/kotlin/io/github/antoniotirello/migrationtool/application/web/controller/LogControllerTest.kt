package io.github.antoniotirello.migrationtool.application.web.controller

import io.github.antoniotirello.migrationtool.application.web.config.TestDatabaseConfig
import io.github.antoniotirello.migrationtool.context.AppBootstrap
import io.github.antoniotirello.migrationtool.context.AppContext
import io.github.antoniotirello.migrationtool.context.AppContextBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
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

                jsonPath("$.page") { value(0) }
                jsonPath("$.size") { value(10) }
                jsonPath("$.totalElements") { value(1) }
                jsonPath("$.totalPages") { value(1) }

                jsonPath("$.content") { isArray() }
                jsonPath("$.content.length()") { value(1) }

                jsonPath("$.content[0].id") { isNumber() }
                jsonPath("$.content[0].startedAt") { isString() }
                jsonPath("$.content[0].lastRun") { value(true) }

                jsonPath("$.content[0].endedAt") { doesNotExist() }
                jsonPath("$.content[0].environment") { doesNotExist() }
            }
    }
}