plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":web"))
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("migrationToolPlugin") {
            id = "io.github.antoniotirello.migrationtool"
            implementationClass = "io.github.antoniotirello.migrationtool.MigrationToolPlugin"
        }
    }
}

// IMPORTANTE: questa configurazione risolve il problema "unspecified"
publishing {
    publications {
        // Il plugin java-gradle-plugin crea già una publication,
        // dobbiamo solo assicurarci che abbia i metadati corretti
        withType<MavenPublication> {
            artifactId = "migrationtool-plugin"
        }
    }
}