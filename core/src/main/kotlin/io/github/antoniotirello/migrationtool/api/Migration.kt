package io.github.antoniotirello.migrationtool.api

interface Migration {
    fun up()
    fun down()
}