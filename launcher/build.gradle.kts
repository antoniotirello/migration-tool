
plugins {
    alias(libs.plugins.kotlin.jvm)
    application
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

application {
    mainClass.set("io.github.antoniotirello.migrationtool.launcher.MainKt")
}

dependencies {
    implementation(libs.kotlin.stdlib)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group as String
            artifactId = "migration-tool-launcher"
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