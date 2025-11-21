#!/usr/bin/env bash
##############################################
# Team20 统一构建脚本（严格插件化支持：core 无编译依赖插件）
# 修复：确保如果存在 mvnw，则使用其绝对路径（避免 cd 导致 ./mvnw 不存在的问题）
##############################################

set -euo pipefail

# Detect mvn wrapper at repo root and use absolute path if found
if [ -f "./mvnw" ]; then
  # use absolute path so subsequent cd won't break wrapper invocation
  MAVEN="$(pwd)/mvnw"
else
  MAVEN="mvn"
fi

MAIN_MODULE_DIR="core"   # core 模块目录名；如你改名请同步修改
PLUGIN_DIR="plugins"     # 插件根目录（脚本会扫描 plugins/* 有 pom.xml 的目录）

usage() {
  cat <<EOF
Usage: $0 {compile|test|package|run|clean|verify|reactor}
  compile|package|test : operate on core module (and build required modules with -am)
  run                  : build core + plugins, assemble classpath and run main (strict pluginization)
  reactor              : build full reactor (all modules) with mvn clean package
EOF
  exit 1
}

# find plugin module directories (relative paths)
discover_plugins() {
  PLUGINS=()
  if [ -d "${PLUGIN_DIR}" ]; then
    for p in "${PLUGIN_DIR}"/*; do
      if [ -d "$p" ] && [ -f "$p/pom.xml" ]; then
        PLUGINS+=("$p")
      fi
    done
  fi
}

# build and copy core runtime dependencies
copy_core_dependencies() {
  # run copy-dependencies using the same MAVEN command but invoked from repo root
  # use -f to point at core/pom.xml so wrapper absolute path still works
  "${MAVEN}" -f "${MAIN_MODULE_DIR}/pom.xml" -q dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="${MAIN_MODULE_DIR}/target/dependency"
}

# assemble runtime classpath
assemble_classpath() {
  CP_ELEMENTS=()

  # core classes
  core_classes="$(pwd)/${MAIN_MODULE_DIR}/target/classes"
  CP_ELEMENTS+=("$core_classes")

  # core runtime dependency jars (copied into core/target/dependency)
  if [ -d "${MAIN_MODULE_DIR}/target/dependency" ]; then
    while IFS= read -r -d '' jar; do
      CP_ELEMENTS+=("$jar")
    done < <(find "${MAIN_MODULE_DIR}/target/dependency" -type f -name '*.jar' -print0)
  fi

  # include each plugin's target/classes and plugin jar(s)
  for p in "${PLUGINS[@]}"; do
    plugin_classes="$(pwd)/${p}/target/classes"
    if [ -d "$plugin_classes" ]; then
      CP_ELEMENTS+=("$plugin_classes")
    fi
    if compgen -G "${p}/target/*.jar" > /dev/null; then
      while IFS= read -r -d '' pj; do
        CP_ELEMENTS+=("$pj")
      done < <(find "${p}/target" -maxdepth 1 -type f -name '*.jar' -print0)
    fi
  done

  # dedupe and join with :
  IFS=$'\n' unique=($(awk '!x[$0]++' <<<"${CP_ELEMENTS[*]}"))
  CP=$(IFS=:; echo "${unique[*]}")
}

# run the application with assembled classpath
run_main() {
  if [ -z "${CP}" ]; then
    echo "ERROR: classpath empty"
    exit 1
  fi

  echo "Running com.team20.editor.Main with classpath:"
  printf '  %s\n' ${CP//:/ } | sed -n '1,40p'
  java -cp "${CP}" com.team20.editor.Main
}

case "${1:-}" in
  compile|build)
    echo "Compiling core (and required modules)..."
    "${MAVEN}" -am -pl "${MAIN_MODULE_DIR}" clean compile
    ;;
  test)
    echo "Running tests (in core module context)..."
    "${MAVEN}" -am -pl "${MAIN_MODULE_DIR}" test
    ;;
  package)
    echo "Packaging core (and required modules)..."
    "${MAVEN}" -am -pl "${MAIN_MODULE_DIR}" clean package
    ;;
  run)
    echo "Strict pluginized run: build core + discovered plugins, assemble classpath and start Main"
    discover_plugins

    modules_csv="${MAIN_MODULE_DIR}"
    for p in "${PLUGINS[@]}"; do
      modules_csv+=",${p}"
    done

    echo "Building modules: ${modules_csv}"
    # build core + plugins
    "${MAVEN}" -am -pl "${modules_csv}" -DskipTests clean package

    echo "Copying core runtime dependencies..."
    copy_core_dependencies

    assemble_classpath
    run_main
    ;;
  clean)
    echo "Cleaning reactor"
    "${MAVEN}" clean
    ;;
  verify)
    echo "Verify (clean + test + package) for core context..."
    "${MAVEN}" -am -pl "${MAIN_MODULE_DIR}" clean verify
    ;;
  reactor)
    echo "Building entire reactor (all modules)..."
    "${MAVEN}" clean package
    ;;
  *)
    usage
    ;;
esac

echo ""
echo "✓ 操作完成！"