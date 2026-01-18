package io.github.antoniotirello.migrationtool.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MigrationToolsApplication

fun main(args: Array<String>) {
    runApplication<MigrationToolsApplication>(*args)
}
