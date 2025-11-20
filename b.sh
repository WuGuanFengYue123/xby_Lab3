#!/usr/bin/env bash
# Migrate provider implementations from core -> plugins/core-impl (strict pluginization)
# - Uses git mv when possible (preserve history)
# - Merges core META-INF/services into plugin services and removes core registrations
# - Skips missing files safely (idempotent)
# - Commits changes if running in a git repo
set -euo pipefail

ROOT="$(pwd)"
GIT_OK=0
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  GIT_OK=1
fi

echo "Starting migration (root=${ROOT})"
echo "Note: commit or stash uncommitted work before running."

# Configure mappings: src:dst (relative to repo root).
# Add entries as needed. Missing src entries will be skipped.
MAPS=(
  "core/src/main/java/com/team20/editor/representation/tree/providers/CoreNodeAdapterProvider.java:plugins/core-impl/src/main/java/com/team20/editor/representation/tree/providers/CoreNodeAdapterProvider.java"
  "core/src/main/java/com/team20/editor/monitoring/logging/ConsoleLogSink.java:plugins/core-impl/src/main/java/com/team20/editor/monitoring/logging/ConsoleLogSink.java"
  "core/src/main/java/com/team20/editor/monitoring/logging/FileLogSink.java:plugins/core-impl/src/main/java/com/team20/editor/monitoring/logging/FileLogSink.java"
  "core/src/main/java/com/team20/editor/infrastructure/persistence/DefaultSerializerProvider.java:plugins/core-impl/src/main/java/com/team20/editor/infrastructure/persistence/DefaultSerializerProvider.java"
  "core/src/main/java/com/team20/editor/infrastructure/persistence/JsonSerializer.java:plugins/core-impl/src/main/java/com/team20/editor/infrastructure/persistence/JsonSerializer.java"
  "core/src/main/java/com/team20/editor/domain/command/impl/logging/LoggingCommandProvider.java:plugins/core-impl/src/main/java/com/team20/editor/domain/command/impl/logging/LoggingCommandProvider.java"
  "core/src/main/java/com/team20/editor/domain/command/impl/workspace/CloseCommand.java:plugins/core-impl/src/main/java/com/team20/editor/domain/command/impl/workspace/CloseCommand.java"
  "core/src/main/java/com/team20/editor/domain/command/impl/workspace/EditCommand.java:plugins/core-impl/src/main/java/com/team20/editor/domain/command/impl/workspace/EditCommand.java"
  "core/src/main/java/com/team20/editor/extension/spi/editor/TextEditorProvider.java:plugins/core-impl/src/main/java/com/team20/editor/extension/spi/editor/TextEditorProvider.java"
)

# Services to merge (filenames under META-INF/services)
SERVICES=(
  "com.team20.editor.extension.spi.command.CommandProvider"
  "com.team20.editor.extension.spi.editor.EditorProvider"
  "com.team20.editor.extension.spi.node.NodeAdapterProvider"
  "com.team20.editor.extension.spi.serialization.SerializerProvider"
  "com.team20.editor.monitoring.logging.LogSink"
)

PLUGIN_SERVICES_DIR="plugins/core-impl/src/main/resources/META-INF/services"
CORE_SERVICES_DIR="core/src/main/resources/META-INF/services"

mkdir -p "${PLUGIN_SERVICES_DIR}"

ensure_parent_dir() {
  local f="$1"
  local p
  p="$(dirname "$f")"
  if [ ! -d "$p" ]; then
    mkdir -p "$p"
  fi
}

moved=0
echo "Moving source files..."
for m in "${MAPS[@]}"; do
  src="${m%%:*}"
  dst="${m#*:}"
  if [ -f "$src" ]; then
    ensure_parent_dir "$dst"
    if [ "$GIT_OK" -eq 1 ]; then
      git mv -f "$src" "$dst"
    else
      mv -f "$src" "$dst"
    fi
    echo "  moved: $src -> $dst"
    moved=1
  else
    echo "  skip (not found): $src"
  fi
done

echo
echo "Merging service registration files..."
for svc in "${SERVICES[@]}"; do
  coref="${CORE_SERVICES_DIR}/${svc}"
  pluginf="${PLUGIN_SERVICES_DIR}/${svc}"

  # gather lines ignoring comments/blank
  core_lines=""
  plugin_lines=""
  if [ -f "$coref" ]; then
    core_lines="$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$coref" || true)"
  fi
  if [ -f "$pluginf" ]; then
    plugin_lines="$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$pluginf" || true)"
  fi

  merged="${plugin_lines}"
  # append unique lines from core
  while IFS= read -r ln; do
    [ -z "$ln" ] && continue
    if ! grep -Fxq "$ln" <<<"${plugin_lines}"; then
      if [ -n "$merged" ]; then
        merged="${merged}"$'\n'"${ln}"
      else
        merged="${ln}"
      fi
    fi
  done <<EOF
$core_lines
EOF

  if [ -n "$(echo "$merged" | sed '/^[[:space:]]*$/d')" ]; then
    ensure_parent_dir "$pluginf"
    # dedupe while writing
    printf "%s\n" "$merged" | awk '!x[$0]++' > "${pluginf}.tmp"
    mv "${pluginf}.tmp" "$pluginf"
    if [ "$GIT_OK" -eq 1 ]; then git add "$pluginf"; fi
    echo "  merged service -> ${pluginf}"
  fi

  # remove core service file (if exists)
  if [ -f "$coref" ]; then
    if [ "$GIT_OK" -eq 1 ]; then
      git rm -f "$coref" || true
    else
      rm -f "$coref" || true
    fi
    echo "  removed core service: ${coref}"
  fi
done

echo
# Check plugin pom dependency on core
if [ -f "plugins/core-impl/pom.xml" ]; then
  if ! grep -q "<artifactId>text-editor</artifactId>" plugins/core-impl/pom.xml && ! grep -q "<artifactId>text-editor</artifactId>" plugins/core-impl/pom.xml ; then
    cat <<EOF

WARNING: plugins/core-impl/pom.xml does not appear to declare a dependency on core/text-editor.
Add the following snippet inside <dependencies> of plugins/core-impl/pom.xml:

    <dependency>
      <groupId>com.team20</groupId>
      <artifactId>text-editor</artifactId>
      <version>\${project.version}</version>
    </dependency>

EOF
  else
    echo "plugins/core-impl/pom.xml declares dependency on core/text-editor (ok)."
  fi
else
  echo "WARNING: plugins/core-impl/pom.xml not found."
fi

# Finalize commit if git available
if [ "$GIT_OK" -eq 1 ]; then
  if [ "$moved" -eq 1 ]; then
    git add -A
    git commit -m "Migrate provider implementations to plugins/core-impl (strict pluginization)"
    echo "Committed migration to git."
  else
    echo "No source files moved; nothing to commit."
  fi
else
  echo "Not a git repo: migration actions performed locally (no commit)."
fi

echo
echo "Migration finished. Suggested next steps:"
echo "  1) Review changes: git status, git diff"
echo "  2) Build & test: ./build.sh package && ./build.sh run"
echo "  3) Run verify: ./verify_plugins.sh"