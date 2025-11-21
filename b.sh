#!/usr/bin/env bash
#
# Migrate concrete implementation sources from core -> plugins/core-impl
# - Safe, idempotent, supports --dry-run
# - Uses git mv when in a git repo (preserve history); otherwise mv
# - Merges META-INF/services entries into plugins/core-impl services
#
# Usage:
#   chmod +x scripts/migrate_all_impls_to_plugins.sh
#   # preview only
#   ./scripts/migrate_all_impls_to_plugins.sh --dry-run
#   # execute
#   ./scripts/migrate_all_impls_to_plugins.sh
#
set -euo pipefail

ROOT="$(pwd)"
DRY_RUN=0
if [ "${1:-}" = "--dry-run" ]; then
  DRY_RUN=1
fi

# detect git repo
GIT_OK=0
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  GIT_OK=1
fi

echo "Migration root: ${ROOT}"
[ "$DRY_RUN" -eq 1 ] && echo "(dry-run mode: no files will be changed)"

# destination base
PLUGIN_BASE="plugins/core-impl"
DEST_BASE="${PLUGIN_BASE}/src/main/java"

# candidate globs (we will find files under these patterns)
CANDIDATE_PATHS=(
  "core/src/main/java/**/domain/command/impl"
  "core/src/main/java/**/monitoring/logging"
  "core/src/main/java/**/infrastructure/persistence"
  "core/src/main/java/**/representation/tree/providers"
  "core/src/main/java/**/domain/command/impl/*"
)

# (excerpt — replace EXCLUDE_NAMES in your script)
EXCLUDE_NAMES=(
  "LogSink.java"
  "Logger.java"
  "LogListener.java"
  "Serializer.java"
  "Command.java"
  "CommandDescriptor.java"
  "CommandInvoker.java"
  "UndoableCommand.java"
  "PersistenceManager.java"   # <-- DO NOT MOVE: keep persistence manager in core
)

# service files to merge
CORE_SERVICES_DIR="core/src/main/resources/META-INF/services"
PLUGIN_SERVICES_DIR="${PLUGIN_BASE}/src/main/resources/META-INF/services"
SERVICES_TO_HANDLE=(
  "com.team20.editor.extension.spi.command.CommandProvider"
  "com.team20.editor.extension.spi.editor.EditorProvider"
  "com.team20.editor.extension.spi.node.NodeAdapterProvider"
  "com.team20.editor.extension.spi.serialization.SerializerProvider"
  "com.team20.editor.monitoring.logging.LogSink"
)

# helper
ensure_parent_dir() {
  local f="$1"
  local p
  p="$(dirname "$f")"
  if [ ! -d "$p" ]; then
    mkdir -p "$p"
  fi
}

is_excluded_name() {
  local name="$1"
  for ex in "${EXCLUDE_NAMES[@]}"; do
    if [ "$ex" = "$name" ]; then
      return 0
    fi
  done
  return 1
}

# gather candidate files
echo "Collecting candidate implementation files..."
FILES_TO_MOVE=()
# use find to locate likely implementation files under impl / providers / logging / persistence
while IFS= read -r -d '' f; do
  fname="$(basename "$f")"
  if is_excluded_name "$fname"; then
    continue
  fi
  # skip interfaces or abstract classes heuristically: check for 'interface' or 'abstract class' line
  if grep -E '^\s*(public\s+)?interface\s+' "$f" >/dev/null 2>&1; then
    continue
  fi
  if grep -E '^\s*(public\s+)?abstract\s+class\s+' "$f" >/dev/null 2>&1; then
    continue
  fi
  FILES_TO_MOVE+=("$f")
done < <(find core/src/main/java -type f -name '*.java' \( -path '*/domain/command/impl/*' -o -path '*/monitoring/logging/*' -o -path '*/infrastructure/persistence/*' -o -path '*/representation/tree/providers/*' \) -print0)

# print summary
echo "Found ${#FILES_TO_MOVE[@]} candidate files to move."
for f in "${FILES_TO_MOVE[@]}"; do
  echo "  - $f"
done

if [ "$DRY_RUN" -eq 1 ]; then
  echo "Dry-run complete. No changes made."
  exit 0
fi

# perform moves
echo
echo "Moving files to ${DEST_BASE} ..."
moved_any=0
for src in "${FILES_TO_MOVE[@]}"; do
  rel="${src#core/}"   # path relative to core/
  dst="${DEST_BASE}/${rel#src/main/java/}"  # strip src/main/java/ from rel
  ensure_parent_dir "$dst"
  if [ "$GIT_OK" -eq 1 ]; then
    git mv -f "$src" "$dst"
  else
    mv -f "$src" "$dst"
  fi
  echo "  moved: $src -> $dst"
  moved_any=1
done

# Merge service files (same behavior as earlier script)
echo
echo "Merging service registration files into ${PLUGIN_SERVICES_DIR} ..."
mkdir -p "${PLUGIN_SERVICES_DIR}"
for svc in "${SERVICES_TO_HANDLE[@]}"; do
  coref="${CORE_SERVICES_DIR}/${svc}"
  pluginf="${PLUGIN_SERVICES_DIR}/${svc}"
  core_lines=""
  plugin_lines=""
  if [ -f "$coref" ]; then
    core_lines="$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$coref" || true)"
  fi
  if [ -f "$pluginf" ]; then
    plugin_lines="$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$pluginf" || true)"
  fi

  merged="${plugin_lines}"
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
    printf "%s\n" "$merged" | awk '!x[$0]++' > "${pluginf}.tmp"
    mv "${pluginf}.tmp" "$pluginf"
    if [ "$GIT_OK" -eq 1 ]; then git add "$pluginf"; fi
    echo "  merged -> ${pluginf}"
  fi

  if [ -f "$coref" ]; then
    if [ "$GIT_OK" -eq 1 ]; then git rm -f "$coref" || true; else rm -f "$coref" || true; fi
    echo "  removed core service: ${coref}"
  fi
done

# Ensure plugin pom depends on core
if [ -f "plugins/core-impl/pom.xml" ]; then
  if ! grep -q "<artifactId>text-editor</artifactId>" plugins/core-impl/pom.xml; then
    cat <<EOF

WARNING: plugins/core-impl/pom.xml does not declare dependency on core/text-editor.
Add inside <dependencies>:
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

# finalize git commit
if [ "$GIT_OK" -eq 1 ]; then
  if [ "$moved_any" -eq 1 ]; then
    git add -A
    git commit -m "Migrate concrete implementations from core to plugins/core-impl (pluginize impls)"
    echo "Committed migration."
  else
    echo "No implementation files moved; nothing to commit."
  fi
else
  echo "Not a git repo: files moved on disk but no commit performed."
fi

echo
echo "Migration finished."
echo "Next steps:"
echo "  1) Add a CommandProvider implementation in plugins/core-impl to register commands (close/edit/append/...)."
echo "  2) Ensure services file plugins/core-impl/src/main/resources/META-INF/services/com.team20.editor.extension.spi.command.CommandProvider contains your provider FQN."
echo "  3) Build & test: ./build.sh package && ./build.sh run"
echo "  4) Run verifier: ./verify_plugins.sh"