package io.github.antoniotirello.migrationtool.service

import io.github.antoniotirello.migrationtool.api.Migration
import io.github.classgraph.ClassGraph

fun getAllMigrationClasses(packageName: String): List<Class<out Migration>> {
    val scanResult = ClassGraph()
        .enableAllInfo() // importante per Spring Boot fat jar
        .acceptPackages(packageName)
        .scan()

    return scanResult
        .getClassesImplementing(Migration::class.java.name)
        .loadClasses(Migration::class.java)
        .sortedBy { it.simpleName }
}

fun runAllMigrations(packageName: String): List<String> {
    val migrationClasses = getAllMigrationClasses(packageName)

    val executed = mutableListOf<String>()

    for (clazz in migrationClasses) {
        val instance = clazz.getDeclaredConstructor().newInstance()
        instance.up() // esegue la migrazione
        executed.add(clazz.simpleName)
    }

    return executed
}