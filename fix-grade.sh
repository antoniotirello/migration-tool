#!/usr/bin/env bash
set -e

echo "Stopping Gradle daemons..."
./gradlew --stop

echo "Cleaning project .gradle folder..."
rm -rf .gradle

echo "Cleaning global Gradle caches..."
rm -rf ~/.gradle/caches
rm -rf ~/.gradle/daemon

echo "Running clean build..."
./gradlew clean build
