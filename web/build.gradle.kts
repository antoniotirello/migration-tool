import org.gradle.api.tasks.WriteProperties

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    `java-library`
    `maven-publish`
}

description = "Optional web backend"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// KTor use shadowJar by default. Disable it because this one isn't an application
tasks.named("shadowJar") {
    enabled = false
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.openapi)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.status.pages)


    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            artifactId = "migrationtool-web"
        }
    }
}

val dev by sourceSets.creating {
    kotlin.srcDir("src/dev/kotlin")
    resources.srcDir("src/dev/resources")

    // inherit classpath from main
    compileClasspath += sourceSets["main"].output + sourceSets["main"].compileClasspath
    runtimeClasspath += sourceSets["main"].output + sourceSets["main"].runtimeClasspath
}

// Dev task to run the backend
tasks.register<JavaExec>("runDev") {
    group = "development"
    classpath = dev.runtimeClasspath
    mainClass.set("io.github.antoniotirello.migrationtool.DevLauncherKt")
}

tasks.withType<JavaExec> {
    systemProperty("app.version", project.version)
}

tasks.register<WriteProperties>("writeBuildInfo") {
    destinationFile.set(
        layout.buildDirectory.file(
            "resources/main/META-INF/build-info.properties"
        )
    )
    property("version", project.version.toString())
    property("name", project.rootProject.name)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn("writeBuildInfo")
}