plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot) apply false

    `jacoco-report-aggregation`
}

repositories {
    mavenCentral()
}

val excludedFromCoverage = setOf(
    "plugin",   // Gradle custom plugin and related tasks
    "launcher"  // Infrastructure code
)

dependencies {
    subprojects
        .filter { it.name !in excludedFromCoverage }
        .forEach { jacocoAggregation(it) }
}

subprojects {
    apply(plugin = "jacoco")

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

reporting {
    // jacoco-report-aggregation is @Incubating since Gradle 7.4,
    // but it is the only configuration-cache compatible approach.
    // Re-evaluate on every Gradle upgrade.
    @Suppress("UnstableApiUsage")
    reports {
        @Suppress("UnstableApiUsage")
        create<JacocoCoverageReport>("jacocoRootReport") {
            testSuiteName = "test"
            reportTask {
                // Exclude DTOs from coverage
                classDirectories.setFrom(
                    classDirectories.files.map { dir ->
                        fileTree(dir) {
                            exclude(
                                "**/dto/**",        // classi in package dto
                                "**/*Dto.class",    // classi che finiscono con Dto
                                "**/*DTO.class",     // classi che finiscono con DTO
                                // Infrastructure / Gradle tasks
                                "**/LaunchMigrationToolTask.class",
                                // ORM table definitions and entity mappings
                                "**/dao/**"
                            )
                        }
                    }
                )

                reports {
                    html.required.set(true)
                    xml.required.set(false)
                    csv.required.set(false)
                }
            }
        }
    }
}

// Dedicated task to print also the report path
tasks.register("printJacocoReport") {
    group = "reporting"
    dependsOn("jacocoRootReport")
    val reportPath = layout.buildDirectory
        .file("reports/jacoco/jacocoRootReport/html/index.html")
        .get().asFile.absolutePath
    doLast {
        println("\n✅ JaCoCo report generated:")
        println("👉 file://$reportPath\n")
    }
}