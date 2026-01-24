package io.github.antoniotirello.migrationtool

import io.github.antoniotirello.migrationtool.api.MigrationLanguage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class MigrationToolExtension @Inject constructor(
    objects: ObjectFactory
) {
    val sourceDir: Property<String> =
        objects.property(String::class.java)

    val packageName: Property<String> =
        objects.property(String::class.java)

    val language: Property<MigrationLanguage> =
        objects.property(MigrationLanguage::class.java)
}