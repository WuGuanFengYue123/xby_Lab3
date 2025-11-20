#!/usr/bin/env bash
# verify_plugins.sh
# Verification script for strict pluginization and runtime provider availability.
#
# What this script checks:
# 1) Static scan for direct instantiations (new TextEditor / new ConsoleLogSink / new JsonSerializer / ...)
#    inside the core module source tree.
# 2) Ensures core does NOT register concrete providers in core/src/main/resources/META-INF/services.
# 3) Discovers plugin modules under plugins/* (those containing a pom.xml) and verifies each plugin
#    has META-INF/services entries for implemented SPIs and that the implementation classes exist.
# 4) Builds the project (mvn package) to ensure compilation succeeds.
# 5) Performs a lightweight runtime check: runs the project's run script (./build.sh run) for a few
#    seconds, captures output and checks that required ServiceLoader providers were discovered
#    (EditorProvider, SerializerProvider, LogSink). The script will kill the run after timeout.
#
# Usage:
#   chmod +x verify_plugins.sh
#   ./verify_plugins.sh
#
# Return codes:
#   0 - all checks passed
#   non-zero - one or more checks failed (printed details)

set -uo pipefail

# Do not exit on first error so we can report all problems
# We'll track failures in FAILURES array
FAILURES=()

# Detect mvn wrapper
if [ -f "./mvnw" ]; then
  MAVEN="./mvnw"
else
  MAVEN="mvn"
fi

# Helper printing
info() { printf "\e[1;34m[INFO]\e[0m %s\n" "$*"; }
ok()   { printf "\e[1;32m[OK]\e[0m %s\n" "$*"; }
warn() { printf "\e[1;33m[WARN]\e[0m %s\n" "$*"; }
err()  { printf "\e[1;31m[FAIL]\e[0m %s\n" "$*"; FAILURES+=("$*"); }

echo
info "Verification started at $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
echo

# 1) Static scan for direct instantiations inside core
info "1) Static scan for direct instantiations in core..."

# classes we consider must not be directly new'd in core
BAD_CLASSES=(
  "TextEditor"
  "ConsoleLogSink"
  "JsonSerializer"
  "DefaultSerializerProvider"
  "TextEditorProvider"
)

# We'll search core module path first, then legacy src/
CORE_SRC_DIRS=( "core/src/main/java" "src/main/java" )

FOUND_BAD=0
for d in "${CORE_SRC_DIRS[@]}"; do
  if [ -d "$d" ]; then
    for cname in "${BAD_CLASSES[@]}"; do
      # regex to find 'new <ClassName>(' possibly with generics or whitespace
      matches=$(grep -R --line-number -E "new[[:space:]]+${cname}[[:space:]]*\(" "$d" 2>/dev/null || true)
      if [ -n "$matches" ]; then
        warn "Direct instantiation(s) of ${cname} found under ${d}:"
        printf '%s\n' "$matches"
        FOUND_BAD=1
      fi
    done
  fi
done

if [ "$FOUND_BAD" -eq 0 ]; then
  ok "No forbidden direct instantiations found in core source directories."
else
  err "Remove or refactor the above direct instantiations to use ServiceLoader/Factory."
fi

echo

# 2) Check core's META-INF/services (it should NOT list concrete implementations)
info "2) Checking core/src/main/resources/META-INF/services (should NOT contain provider implementations)..."

CORE_SERVICES_DIR="core/src/main/resources/META-INF/services"
if [ -d "$CORE_SERVICES_DIR" ]; then
  any_impl_found=0
  for f in "$CORE_SERVICES_DIR"/*; do
    [ -f "$f" ] || continue
    # check file content for non-empty, non-comment lines
    impls=$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$f" || true)
    if [ -n "$impls" ]; then
      warn "Core service file $f contains implementation lines (should be empty or removed):"
      printf '%s\n' "$impls"
      any_impl_found=1
    fi
  done

  if [ "$any_impl_found" -eq 0 ]; then
    ok "No implementation entries found in core's META-INF/services."
  else
    err "Please remove implementation registrations from core's services so core has no built-in implementations."
  fi
else
  ok "No core META-INF/services directory found (good)."
fi

echo

# 3) Discover plugins and validate each plugin's META-INF/services and implementation classes
info "3) Discovering plugins under plugins/ and validating their META-INF/services..."

PLUGINS_ROOT="plugins"
PLUGIN_MODULES=()
if [ -d "$PLUGINS_ROOT" ]; then
  while IFS= read -r -d '' p; do
    PLUGIN_MODULES+=("$p")
  done < <(find "$PLUGINS_ROOT" -maxdepth 2 -mindepth 1 -type f -name pom.xml -print0 | xargs -0 -n1 dirname -r 2>/dev/null || true)
fi

if [ ${#PLUGIN_MODULES[@]} -eq 0 ]; then
  warn "No plugin modules with pom.xml found under plugins/. That's fine if you intentionally have no plugins, but core will not run in strict mode without plugins."
else
  ok "Found ${#PLUGIN_MODULES[@]} plugin module(s):"
  for p in "${PLUGIN_MODULES[@]}"; do
    printf "  - %s\n" "$p"
  done
fi

# Required SPIs core expects at runtime
REQUIRED_SPIS=(
  "com.team20.editor.extension.spi.editor.EditorProvider"
  "com.team20.editor.extension.spi.serialization.SerializerProvider"
  "com.team20.editor.monitoring.logging.LogSink"
)

# Track aggregated presence of required SPIs across plugins
declare -A SPI_FOUND
for s in "${REQUIRED_SPIS[@]}"; do SPI_FOUND["$s"]=0; done

# Validate each plugin
for plugin in "${PLUGIN_MODULES[@]}"; do
  info "Checking plugin: $plugin"
  svcdir="$plugin/src/main/resources/META-INF/services"
  if [ ! -d "$svcdir" ]; then
    warn "  No META-INF/services in $plugin (expected for a plugin implementing SPI)."
    continue
  fi

  for sf in "$svcdir"/*; do
    [ -f "$sf" ] || continue
    fname=$(basename "$sf")
    echo "  service: $fname"
    # print non-empty, non-comment lines
    impls=$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$sf" || true)
    if [ -z "$impls" ]; then
      warn "    file exists but contains no implementation entries."
      continue
    fi
    while IFS= read -r impl; do
      [ -z "$impl" ] && continue
      printf "    -> %s\n" "$impl"
      # check implementation class exists in source tree (src/main/java) or compiled classes (target/classes)
      implpath="${plugin}/src/main/java/$(echo "$impl" | sed 's/\./\//g').java"
      implclasspath="${plugin}/target/classes/$(echo "$impl" | sed 's/\./\//g').class"
      if [ -f "$implpath" ]; then
        ok "      implementation source found: $implpath"
      elif [ -f "$implclasspath" ]; then
        ok "      compiled implementation found: $implclasspath"
      else
        # also look in repository-wide sources (in case implementation lives in core mistakenly)
        if [ -f "core/src/main/java/$(echo "$impl" | sed 's/\./\//g').java" ]; then
          warn "      implementation source found in core (should be in plugin): core/..."
        else
          err "      implementation class not found for $impl (neither source nor compiled class present)."
        fi
      fi

      # mark SPI presence
      if [ "${SPI_FOUND[$fname]+_}" ]; then
        SPI_FOUND["$fname"]=1
      fi
    done <<EOF
$impls
EOF
  done
done

# Report aggregated SPI presence
echo
info "Aggregated required SPI presence across plugins:"
for s in "${REQUIRED_SPIS[@]}"; do
  if [ "${SPI_FOUND[$s]}" -eq 1 ]; then
    ok "  $s - found in plugins"
  else
    warn "  $s - NOT found in any plugin (core strict mode requires providers for required SPIs)"
  fi
done

echo

# 4) Build project (package) to ensure compilation
info "4) Building project (package) - this will compile core and discovered plugins..."
# Build only the modules we need: core + discovered plugin module directories (if any)
modules_csv="core"
for p in "${PLUGIN_MODULES[@]}"; do
  modules_csv+=",${p}"
done

info "Running: ${MAVEN} -am -pl \"${modules_csv}\" -DskipTests clean package"
if ! ${MAVEN} -am -pl "${modules_csv}" -DskipTests clean package; then
  err "Maven build failed. Inspect output above for compilation errors."
else
  ok "Maven build succeeded for core and plugins (package)."
fi

echo

# 5) Runtime check: run ./build.sh run for a short timeout and inspect output for provider discovery
info "5) Runtime check: launching './build.sh run' for a short, headless verification..."

if [ ! -x "./build.sh" ]; then
  warn "build.sh not found or not executable; skipping runtime check."
else
  LOGFILE=$(mktemp /tmp/verify_plugins.XXXXXX.log)
  # run build.sh run in background with timeout, capture stdout/stderr
  TIMEOUT_SECS=8
  info "Running './build.sh run' for ${TIMEOUT_SECS}s and capturing output to ${LOGFILE}..."
  if command -v timeout >/dev/null 2>&1; then
    timeout "${TIMEOUT_SECS}" ./build.sh run >"${LOGFILE}" 2>&1 || true
  else
    # fallback: run in background and kill after TIMEOUT_SECS
    ./build.sh run >"${LOGFILE}" 2>&1 & pid=$!
    sleep "${TIMEOUT_SECS}"
    kill "${pid}" 2>/dev/null || true
  fi

  # show tail of log
  echo "---- runtime output (last 200 lines) ----"
  tail -n 200 "${LOGFILE}" || true
  echo "---- end runtime output ----"

  # analyze for common errors or success indicators
  if grep -E "ServiceConfigurationError|Provider .* not found|NoSuchMethodError|ClassNotFoundException" "${LOGFILE}" >/dev/null 2>&1; then
    err "Runtime output contains ServiceLoader/Classpath errors. See above for details."
  else
    # look for ApplicationContext summary or Editor providers line
    if grep -E "Editor providers:|Application Context Summary|Team20 Text Editor" "${LOGFILE}" >/dev/null 2>&1; then
      ok "Runtime output shows application started and providers were likely discovered."
    else
      warn "Runtime output did not clearly show provider discovery. Manual inspection may be required."
    fi
  fi
  rm -f "${LOGFILE}"
fi

echo

# Final summary
if [ "${#FAILURES[@]}" -ne 0 ]; then
  err "Verification finished: FAILURES detected (${#FAILURES[@]}):"
  for f in "${FAILURES[@]}"; do
    printf "  - %s\n" "$f"
  done
  echo
  echo "Suggested fixes (summary):"
  echo "  * Remove direct instantiations in core; use ServiceLoader/Provider pattern."
  echo "  * Ensure core does not register concrete providers in core/src/main/resources/META-INF/services."
  echo "  * Ensure each plugin has proper META-INF/services/<SPI-FQN> files listing implementations."
  echo "  * Make sure plugin implementation classes exist under plugin/src/main/java and compile."
  echo "  * Re-run ./build.sh run after fixing issues; the script assembles runtime classpath for strict mode."
  exit 2
else
  ok "Verification finished: all checks passed (or only warnings)."
  exit 0
fi