package io.github.antoniotirello.migrationtool.application.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class TestResponse(val message: String)

@Tag(name = "Test", description = "Endpoint di test rapido xxxXXXxxx")
@RestController
@RequestMapping("/api/test")
class TestController {

    @Operation(summary = "Check backend status")
    @GetMapping
    fun hello(): TestResponse {
        return TestResponse("Ciao! Il backend funziona!!! 🚀")
    }
}