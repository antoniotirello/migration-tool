
plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`

    `maven-publish`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.classgraph)
}

publishing {
    publications {
        create<MavenPublication>("core") {
            from(components["java"])
            groupId = "io.github.antoniotirello.migrationtool"
            artifactId = "core"
            version = project.version.toString()
        }
    }
    repositories {
        maven {
            name = "local"
            url = uri("${rootProject.projectDir}/repository")
        }
    }
}


// Generates MigrationToolInfo.kt with the library's actual version
// included in the JAR. Required for LaunchMigrationToolTask and to avoid
// dependencies on external properties.
//
// Note: the folder is under build/, so after `clean` it will be regenerated
// automatically before compilation thanks to compileKotlin.dependsOn

val generateVersionFile by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated-src/version")
    val versionProvider = project.providers.provider { project.version.toString() }

    inputs.property("libVersion", versionProvider)
    outputs.dir(outputDir)

    doLast {
        val file = outputDir.get().file("MigrationToolInfo.kt").asFile
        file.parentFile.mkdirs()
        file.writeText("""
            package io.github.antoniotirello.migrationtool

            object MigrationToolInfo {
                const val VERSION = "${versionProvider.get()}"
            }
        """.trimIndent())
    }
}

sourceSets.main.get().kotlin.srcDir(layout.buildDirectory.dir("generated-src/version"))


tasks.named("compileKotlin") {
    dependsOn(generateVersionFile)
}