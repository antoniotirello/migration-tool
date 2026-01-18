// web/build.gradle.kts
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
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
     implementation(project(":core"))
    // implementation(project(":store"))

    implementation(libs.spring.boot.core)
    implementation(libs.spring.boot.web)
    implementation(libs.spring.boot.actuator)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.springdoc.openapi.ui)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.kotlin.test)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.test {
    useJUnitPlatform()
}

// Crea un BootJar eseguibile
tasks.named<BootJar>("bootJar") {
    enabled = true
    archiveClassifier.set("boot")
}

// Crea anche un JAR normale per dipendenze
tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}

publishing {
    publications {
        create<MavenPublication>("boot") {
            artifact(tasks.named<BootJar>("bootJar"))

            groupId = "io.github.antoniotirello.migrationtool"
            artifactId = "web"
            version = "0.0.1"
        }
    }
}