package io.github.antoniotirello.migrationtool.net

fun getRandomFreePort(): Int =
    java.net.ServerSocket(0).use { it.localPort }