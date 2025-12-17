# MigrationTool

## Introduction

MigrationTool is a database schema migration tool with the following characteristics:

- Database-agnostic
- Migrations written in Java/Kotlin (allowing custom logic when needed)
- No raw SQL
- No XML
- Optional GUI

## Why

In the past, I used Flyway (which relies on plain SQL) and Liquibase (which uses XML) to manage
database schema changes, but neither fully met my needs. This led me to develop this tool.

Flyway caused portability issues: migrations written in pure SQL are not easily portable across
different databases. For example, migrations written for PostgreSQL could not be reused as-is
with an in-memory SQLite database for testing.

Liquibase, on the other hand, is database-agnostic, which I appreciate, but its XML-based
approach is verbose and not particularly developer-friendly.

When working with Laravel (PHP), migrations are written as code. This provides full IDE support
(e.g. code completion), keeps migrations database-agnostic, and allows embedding logic directly
in the migration itself. MigrationTool is inspired by this approach.
