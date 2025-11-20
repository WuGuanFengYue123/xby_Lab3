#!/usr/bin/env bash
# verify_plugins.sh - 静态与运行时检查：确认无内置实现并且 SPI 提供者存在
set -euo pipefail

ROOT="$(pwd)"
echo "=== Pluginization verification ==="
echo

# 1) 静态搜索：查找硬编码 new 某些具体实现的地方
echo "1) Static search for direct instantiations (new TextEditor / new ConsoleLogSink / new JsonSerializer / new ...)..."
FOUND=$(grep -R --line-number -E "new\s+(TextEditor|ConsoleLogSink|FileLogSink|JsonSerializer|JsonSerializerProvider|TextEditorProvider|ConsoleLogSink|LoggingCommandProvider)" src || true)
if [ -z "$FOUND" ]; then
  echo "  OK: no direct instantiation of known concrete implementations found."
else
  echo "  WARNING: found direct instantiations (these should be removed or replaced by SPI/factory):"
  echo "$FOUND"
fi
echo

# 2) Check META-INF/services presence in compiled classes (target)
echo "2) Check META-INF/services in target/classes (runtime SPI registration)"
if [ -d target/classes/META-INF/services ]; then
  echo "  Files under target/classes/META-INF/services:"
  ls -1 target/classes/META-INF/services || true
else
  echo "  Not found: target/classes/META-INF/services (no service declarations in compiled classes)"
fi
echo

# 3) Compile to ensure code compiles without bundled implementations
echo "3) mvn -q -DskipTests compile"
if mvn -q -DskipTests compile; then
  echo "  OK: compile succeeded"
else
  echo "  ERROR: compile failed"
  exit 2
fi
echo

# 4) Runtime check: start app and capture initial ApplicationContext dump or error
echo "4) Runtime check: starting app to verify ServiceLoader providers presence..."
# Try to run with short timeout - run build.sh run which runs the app; we capture first 10 lines of output
if ./build.sh run >/tmp/plugin_verification_run.log 2>&1; then
  echo "  App started successfully. Check ApplicationContext summary below (first 80 lines):"
  sed -n '1,80p' /tmp/plugin_verification_run.log
else
  echo "  App failed to start. Check log for reason (first 120 lines):"
  sed -n '1,120p' /tmp/plugin_verification_run.log || true
  echo "  If failure message indicates missing EditorProvider/CommandProvider/etc, SPI not satisfied."
fi
echo

# 5) Suggest adding unit test integration if all ok
echo "5) Recommendation: add an integration test that asserts ServiceLoader returns non-empty iterators for required SPIs."
echo "=== End of verification ==="
