#!/usr/bin/env bash

set -euo pipefail

# ---- Parametri ----
PROJECT1_DIR="${1:-}"
PROJECT2_DIR="${2:-}"
GRADLE_TASK="${3:-}"

# ---- Validazione input ----
if [[ -z "$PROJECT1_DIR" || -z "$PROJECT2_DIR" || -z "$GRADLE_TASK" ]]; then
  echo "Uso:"
  echo "  $0 <cartella_progetto_1> <cartella_progetto_2> <task_gradle>"
  echo "Esempio:"
  echo "  $0 ../lib-project ../app-project openMigrationTool"
  exit 1
fi

if [[ ! -d "$PROJECT1_DIR" ]]; then
  echo "❌ Cartella progetto 1 non trovata: $PROJECT1_DIR"
  exit 1
fi

if [[ ! -d "$PROJECT2_DIR" ]]; then
  echo "❌ Cartella progetto 2 non trovata: $PROJECT2_DIR"
  exit 1
fi

# ---- Progetto 1 ----
echo "📦 Progetto 1: build + publishToMavenLocal"
cd "$PROJECT1_DIR"

./gradlew clean build publishToMavenLocal

# ---- Progetto 2 ----
echo "🚀 Progetto 2: build + task '$GRADLE_TASK'"
cd "$PROJECT2_DIR"

./gradlew clean build
./gradlew "$GRADLE_TASK" -PopenBrowser=firefox

echo "✅ Operazione completata con successo"
