#!/usr/bin/env bash

set -euo pipefail

# ---- Parameters ----
PROJECT1_DIR="${1:-}"
PROJECT2_DIR="${2:-}"
GRADLE_TASK="${3:-}"

# ---- Input validation ----
if [[ -z "$PROJECT1_DIR" || -z "$PROJECT2_DIR" || -z "$GRADLE_TASK" ]]; then
  echo "Usage:"
  echo "  $0 <project_1_dir> <project_2_dir> <gradle_task>"
  echo "Example:"
  echo "  $0 ../lib-project ../app-project openMigrationTool"
  exit 1
fi

if [[ ! -d "$PROJECT1_DIR" ]]; then
  echo "❌ Project 1 directory not found: $PROJECT1_DIR"
  exit 1
fi

if [[ ! -d "$PROJECT2_DIR" ]]; then
  echo "❌ Project 2 directory not found: $PROJECT2_DIR"
  exit 1
fi

# ---- Project 1 ----
echo "📦 Project 1: build + publishToMavenLocal"
cd "$PROJECT1_DIR"

./gradlew clean build koverHtmlReport koverXmlReport publishToMavenLocal
REPORT_URL="file://$PROJECT1_DIR/core/build/reports/kover/html/index.html"

echo "📊 Opening report..."

if command -v open >/dev/null 2>&1; then
  if open -Ra "Firefox" >/dev/null 2>&1; then
    open -a "Firefox" "$REPORT_URL"
  else
    echo "⚠️ Firefox not found, using default browser"
    open "$REPORT_URL"
  fi
else
  echo "⚠️ 'open' command not found"
fi

# ---- Project 2 ----
echo "🚀 Project 2: build + task '$GRADLE_TASK'"
cd "$PROJECT2_DIR"

./gradlew clean
./gradlew "$GRADLE_TASK" -PopenBrowser=firefox -PdevPort=8888

echo "✅ Operation completed successfully"
