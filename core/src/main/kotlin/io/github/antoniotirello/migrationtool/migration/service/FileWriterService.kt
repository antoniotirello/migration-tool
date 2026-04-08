package io.github.antoniotirello.migrationtool.migration.service

import java.io.File

class FileWriterService {

    fun write(path: String, content: String) {
        File(path).writeText(content)
    }
}