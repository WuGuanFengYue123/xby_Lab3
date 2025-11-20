#!/usr/bin/env bash
# Migrate provider implementations from core to plugins/core-impl (strict pluginization)
# - Uses git mv when possible to preserve history; falls back to mv if not a git repo.
# - Merges/creates META-INF/services files in plugins/core-impl (avoids duplicates).
# - Removes implementation registrations from core's META-INF/services.
#
# Usage:
#   chmod +x migrate_providers_to_plugins.sh
#   ./migrate_providers_to_plugins.sh
#
# IMPORTANT: Commit or stash any uncommitted work before running.

set -euo pipefail

ROOT="$(pwd)"
GIT_OK=0
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  GIT_OK=1
fi

echo "Running migration at: ${ROOT}"
echo

# List of source:destination mappings (relative to repo root)
# Add or remove entries as needed. Script will skip non-existent sources.
MAPS=(
  "core/src/main/java/com/team20/editor/representation/tree/providers/CoreNodeAdapterProvider.java:plugins/core-impl/src/main/java/com/team20/editor/representation/tree/providers/CoreNodeAdapterProvider.java"
  "core/src/main/java/com/team20/editor/monitoring/logging/ConsoleLogSink.java:plugins/core-impl/src/main/java/com/team20/editor/monitoring/logging/ConsoleLogSink.java"
  "core/src/main/java/com/team20/editor/monitoring/logging/FileLogSink.java:plugins/core-impl/src/main/java/com/team20/editor/monitoring/logging/FileLogSink.java"
  "core/src/main/java/com/team20/editor/infrastructure/persistence/DefaultSerializerProvider.java:plugins/core-impl/src/main/java/com/team20/editor/infrastructure/persistence/DefaultSerializerProvider.java"
  "core/src/main/java/com/team20/editor/infrastructure/persistence/JsonSerializer.java:plugins/core-impl/src/main/java/com/team20/editor/infrastructure/persistence/JsonSerializer.java"
  "core/src/main/java/com/team20/editor/domain/command/impl/logging/LoggingCommandProvider.java:plugins/core-impl/src/main/java/com/team20/editor/domain/command/impl/logging/LoggingCommandProvider.java"
  # add more mappings here if you identify other concrete providers in core
)

# Service files to migrate: each entry is interface-filename (path under META-INF/services)
SERVICES_TO_HANDLE=(
  "com.team20.editor.extension.spi.command.CommandProvider"
  "com.team20.editor.extension.spi.editor.EditorProvider"
  "com.team20.editor.extension.spi.node.NodeAdapterProvider"
  "com.team20.editor.extension.spi.serialization.SerializerProvider"
  "com.team20.editor.monitoring.logging.LogSink"
)

# Ensure plugin services directory exists
PLUGIN_SERVICES_DIR="plugins/core-impl/src/main/resources/META-INF/services"
mkdir -p "${PLUGIN_SERVICES_DIR}"

# Helper: ensure parent dir exists
ensure_parent_dir() {
  local dest="$1"
  local parent
  parent="$(dirname "$dest")"
  if [ ! -d "$parent" ]; then
    mkdir -p "$parent"
  fi
}

# Move files preserving git history if possible
moved_any=0
echo "Moving implementation source files..."
for m in "${MAPS[@]}"; do
  src="${m%%:*}"
  dst="${m#*:}"
  if [ -f "$src" ]; then
    ensure_parent_dir "$dst"
    if [ "$GIT_OK" -eq 1 ]; then
      echo "  git mv $src -> $dst"
      git mv "$src" "$dst"
    else
      echo "  mv $src -> $dst"
      mv "$src" "$dst"
    fi
    moved_any=1
  else
    echo "  skip (not found): $src"
  fi
done

if [ "$moved_any" -eq 0 ]; then
  echo "No implementation source files were moved (none of configured sources existed)."
else
  echo "Source file moves done."
fi

echo
echo "Merging service registration entries from core -> plugins/core-impl and removing core registrations..."

CORE_SERVICES_DIR="core/src/main/resources/META-INF/services"

for svc in "${SERVICES_TO_HANDLE[@]}"; do
  core_file="${CORE_SERVICES_DIR}/${svc}"
  plugin_file="${PLUGIN_SERVICES_DIR}/${svc}"

  # collect existing implementations
  impls_core=""
  impls_plugin=""
  if [ -f "$core_file" ]; then
    impls_core="$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$core_file" || true)"
  fi
  if [ -f "$plugin_file" ]; then
    impls_plugin="$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$plugin_file" || true)"
  fi

  # merge unique lines: plugin impls take precedence; add from core if missing
  merged="$impls_plugin"
  while IFS= read -r line; do
    [ -z "$line" ] && continue
    # skip if already present in plugin file
    if ! grep -Fxq "$line" <<<"$impls_plugin"; then
      merged="${merged}"$'\n'"${line}"
    fi
  done <<EOF
$impls_core
EOF

  # write merged back to plugin service file if non-empty
  if [ -n "$(echo "$merged" | sed '/^[[:space:]]*$/d')" ]; then
    ensure_parent_dir "$plugin_file"
    printf "%s\n" "$merged" | awk '!x[$0]++' >"${plugin_file}.tmp"
    mv "${plugin_file}.tmp" "$plugin_file"
    if [ "$GIT_OK" -eq 1 ]; then
      git add "$plugin_file"
    fi
    echo "  merged -> ${plugin_file}"
  fi

  # remove core service file if exists
  if [ -f "$core_file" ]; then
    if [ "$GIT_OK" -eq 1 ]; then
      git rm -f "$core_file" || true
      # if parent META-INF/services dir now empty in core, remove it
      rmdir --ignore-fail-on-non-empty "$(dirname "$core_file")" 2>/dev/null || true
    else
      rm -f "$core_file" || true
    fi
    echo "  removed core service: ${core_file}"
  fi
done

echo
echo "Sanity checks: ensure plugins/core-impl/pom.xml depends on core (SPI) so it can compile."
if [ -f "plugins/core-impl/pom.xml" ]; then
  if ! grep -q "<artifactId>text-editor</artifactId>" plugins/core-impl/pom.xml; then
    echo "  Warning: plugins/core-impl/pom.xml does not declare a dependency on core/text-editor. You should add:"
    cat <<EOF
    <dependency>
      <groupId>com.team20</groupId>
      <artifactId>text-editor</artifactId>
      <version>\${project.version}</version>
    </dependency>
EOF
  else
    echo "  plugins/core-impl/pom.xml declares dependency on core/text-editor (ok)."
  fi
else
  echo "  Warning: plugins/core-impl/pom.xml not found. Ensure plugin module exists and declares core as dependency."
fi

echo
if [ "$GIT_OK" -eq 1 ]; then
  echo "Committing changes to git..."
  git add -A
  git commit -m "Migrate provider implementations from core to plugins/core-impl and register services in plugin"
  echo "Git commit created."
else
  echo "Not a git repo: changes made locally but not committed."
fi

echo
echo "Done. Next recommended steps:"
echo "  1) Review changes (git status / git diff) and run tests/build:"
echo "       ./build.sh package"
echo "  2) Run the strict run to ensure ServiceLoader finds plugin providers:"
echo "       ./build.sh run"
echo "  3) If everything OK, push changes: git push"
echo