#!/usr/bin/env bash
# verify_plugins.sh
# Verification script for strict pluginization and runtime provider availability.
# (fixed plugin discovery logic to be portable and reliable)
set -uo pipefail

FAILURES=()

# Detect mvn wrapper
if [ -f "./mvnw" ]; then
  MAVEN="./mvnw"
else
  MAVEN="mvn"
fi

info() { printf "\e[1;34m[INFO]\e[0m %s\n" "$*"; }
ok()   { printf "\e[1;32m[OK]\e[0m %s\n" "$*"; }
warn() { printf "\e[1;33m[WARN]\e[0m %s\n" "$*"; }
err()  { printf "\e[1;31m[FAIL]\e[0m %s\n" "$*"; FAILURES+=("$*"); }

echo
info "Verification started at $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
echo

# 1) Static scan for direct instantiations inside core
info "1) Static scan for direct instantiations in core..."

BAD_CLASSES=( "TextEditor" "ConsoleLogSink" "JsonSerializer" "DefaultSerializerProvider" "TextEditorProvider" )
CORE_SRC_DIRS=( "core/src/main/java" "src/main/java" )

FOUND_BAD=0
for d in "${CORE_SRC_DIRS[@]}"; do
  if [ -d "$d" ]; then
    for cname in "${BAD_CLASSES[@]}"; do
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

# --- Robust plugin discovery: find pom.xml under plugins/* and convert to module dir ---
# We avoid using xargs tricks or non-portable dirname options.
if [ -d "${PLUGINS_ROOT}" ]; then
  # find depth 2 (plugins/<module>/pom.xml) but allow deeper modules if needed: use -maxdepth 3 as safe option
  # Adjust maxdepth if your plugin layout is deeper.
  while IFS= read -r -d '' pomfile; do
    # get containing directory
    moddir="$(dirname "$pomfile")"
    PLUGIN_MODULES+=("$moddir")
  done < <(find "${PLUGINS_ROOT}" -maxdepth 3 -mindepth 1 -type f -name "pom.xml" -print0 2>/dev/null || true)
fi

if [ ${#PLUGIN_MODULES[@]} -eq 0 ]; then
  warn "No plugin modules with pom.xml found under plugins/. That's fine if you intentionally have no plugins, but core will not run in strict mode without plugins."
else
  ok "Found ${#PLUGIN_MODULES[@]} plugin module(s):"
  for p in "${PLUGIN_MODULES[@]}"; do
    printf "  - %s\n" "$p"
  done
fi

REQUIRED_SPIS=(
  "com.team20.editor.extension.spi.editor.EditorProvider"
  "com.team20.editor.extension.spi.serialization.SerializerProvider"
  "com.team20.editor.monitoring.logging.LogSink"
)

declare -A SPI_FOUND
for s in "${REQUIRED_SPIS[@]}"; do SPI_FOUND["$s"]=0; done

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
    impls=$(sed -n '/^[[:space:]]*#/d;/^[[:space:]]*$/d;p' "$sf" || true)
    if [ -z "$impls" ]; then
      warn "    file exists but contains no implementation entries."
      continue
    fi
    while IFS= read -r impl; do
      [ -z "$impl" ] && continue
      printf "    -> %s\n" "$impl"
      implpath="${plugin}/src/main/java/$(echo "$impl" | sed 's/\./\//g').java"
      implclasspath="${plugin}/target/classes/$(echo "$impl" | sed 's/\./\//g').class"
      if [ -f "$implpath" ]; then
        ok "      implementation source found: $implpath"
      elif [ -f "$implclasspath" ]; then
        ok "      compiled implementation found: $implclasspath"
      else
        if [ -f "core/src/main/java/$(echo "$impl" | sed 's/\./\//g').java" ]; then
          warn "      implementation source found in core (should be in plugin): core/..."
        else
          err "      implementation class not found for $impl (neither source nor compiled class present)."
        fi
      fi

      if [ "${SPI_FOUND[$fname]+_}" ]; then
        SPI_FOUND["$fname"]=1
      fi
    done <<EOF
$impls
EOF
  done
done

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

# 4) Build project (package)
info "4) Building project (package) - this will compile core and discovered plugins..."
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
  TIMEOUT_SECS=8
  info "Running './build.sh run' for ${TIMEOUT_SECS}s and capturing output to ${LOGFILE}..."
  if command -v timeout >/dev/null 2>&1; then
    timeout "${TIMEOUT_SECS}" ./build.sh run >"${LOGFILE}" 2>&1 || true
  else
    ./build.sh run >"${LOGFILE}" 2>&1 & pid=$!
    sleep "${TIMEOUT_SECS}"
    kill "${pid}" 2>/dev/null || true
  fi

  echo "---- runtime output (last 200 lines) ----"
  tail -n 200 "${LOGFILE}" || true
  echo "---- end runtime output ----"

  if grep -E "ServiceConfigurationError|Provider .* not found|NoSuchMethodError|ClassNotFoundException" "${LOGFILE}" >/dev/null 2>&1; then
    err "Runtime output contains ServiceLoader/Classpath errors. See above for details."
  else
    if grep -E "Editor providers:|Application Context Summary|Team20 Text Editor" "${LOGFILE}" >/dev/null 2>&1; then
      ok "Runtime output shows application started and providers were likely discovered."
    else
      warn "Runtime output did not clearly show provider discovery. Manual inspection may be required."
    fi
  fi
  rm -f "${LOGFILE}"
fi

echo

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