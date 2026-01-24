plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))

    implementation(libs.jackson.kotlin)
}

gradlePlugin {
    plugins {
        register("migrationToolPlugin") {
            id = "io.github.antoniotirello.migrationtool"
            implementationClass = "io.github.antoniotirello.migrationtool.plugin.MigrationToolPlugin"
            displayName = "Migration Tool Plugin"
            description = "Plugin for launching the migration tool"
        }
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "io.github.antoniotirello.migrationtool"
            artifactId = "migrationtool-plugin"
            version = project.version.toString()
        }
    }
}

// JAR pulito del plugin - SENZA includere il web
tasks.jar {
    from(sourceSets.main.get().output)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}