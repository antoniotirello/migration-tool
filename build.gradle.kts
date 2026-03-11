// This file declares the main plugins for the multi-module project (Kotlin + Spring Boot)
// without applying them to the root project.
//
// - alias(libs.plugins.*) → points to the version catalog to centralize plugin versions
// - apply false → makes the plugin available to subprojects without applying it to the root
//
// Benefits:
// 1. Subprojects can use the same plugins without redefining versions
// 2. Avoids applying unnecessary plugins to the root, which usually contains no executable code
// 3. Keeps the project cleaner and easier to maintain

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot) apply false
}