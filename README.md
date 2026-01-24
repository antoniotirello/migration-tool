# migration-tool

## Installation

### Plugin

Please, add the following plugin:
```kotlin
id("io.github.antoniotirello.migrationtool") version "0.0.1"
```
Please note, use the correct version: the plugin will follow the same version
number of the whole library.

### Dependency

Please, add the following dependencies:
```kotlin
dependencies {
    implementation("io.github.antoniotirello.migrationtool", "core", "0.0.1")
    implementation("io.github.antoniotirello.migrationtool", "migrationtool-plugin", "0.0.1")
}
```
Please note, use the correct version: the plugin will follow the same version
number of the whole library.

The library is published as modules, the exact module list that you should
add will be assessed.

### Configuration

Please, add the following configuration to your build:
```kotlin
migrationTool {
	sourceDir.set("src/main/kotlin/io/github/company/project/module/mig")
	packageName.set("io.github.company.project.mig")
	language.set(MigrationLanguage.KOTLIN)
}
```

#### sourceDir

this is the path, relative to your project, where the migration files will
be created. You could use Windows or Unix path separator or a mix of both.
An absolute path should work as well but you are making your build 
non-portable.

#### packageName

This is the package name that will be used to qualify the migration files
created. Feel free to use what is better suited for your organization.

#### language

This state the language that you want to use to write the new migration files.
Possible values:

- JAVA
- KOTLIN