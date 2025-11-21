#!/usr/bin/env bash
##############################################
# Team20 统一构建脚本（严格插件化支持：core 无编译依赖插件）
# 改进：为 dependency:copy-dependencies 传入绝对 outputDirectory
##############################################

set -euo pipefail

if [ -f "./mvnw" ]; then
  MAVEN="$(pwd)/mvnw"
else
  MAVEN="mvn"
fi

MAIN_MODULE_DIR="core"
PLUGIN_DIR="plugins"

usage() {
  cat <<EOF
Usage: $0 {compile|test|package|run|clean|verify|reactor}
EOF
  exit 1
}

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

copy_core_dependencies() {
  "${MAVEN}" -f "${MAIN_MODULE_DIR}/pom.xml" -q dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="$(pwd)/${MAIN_MODULE_DIR}/target/dependency"
}

# (片段) copy_plugin_dependencies 函数：请把原来函数替换为下面实现
copy_plugin_dependencies() {
  for p in "${PLUGINS[@]}"; do
    echo "Copying runtime dependencies for plugin: ${p}"
    outdir="$(pwd)/${p}/target/dependency"
    mkdir -p "${outdir}"
    # Exclude core artifact(s) to avoid duplicate/overriding classes at runtime
    "${MAVEN}" -f "${p}/pom.xml" -q dependency:copy-dependencies \
      -DincludeScope=runtime \
      -DexcludeGroupIds=com.team20 \
      -DexcludeArtifactIds=text-editor \
      -DoutputDirectory="${outdir}"
  done
}

assemble_classpath() {
  CP_ELEMENTS=()

  CP_ELEMENTS+=("$(pwd)/${MAIN_MODULE_DIR}/target/classes")

  if [ -d "${MAIN_MODULE_DIR}/target/dependency" ]; then
    while IFS= read -r -d '' jar; do CP_ELEMENTS+=("$jar"); done < <(find "${MAIN_MODULE_DIR}/target/dependency" -type f -name '*.jar' -print0)
  fi

  for p in "${PLUGINS[@]}"; do
    if [ -d "$(pwd)/${p}/target/classes" ]; then
      CP_ELEMENTS+=("$(pwd)/${p}/target/classes")
    fi
    if compgen -G "${p}/target/*.jar" > /dev/null; then
      while IFS= read -r -d '' pj; do CP_ELEMENTS+=("$pj"); done < <(find "${p}/target" -maxdepth 1 -type f -name '*.jar' -print0)
    fi
    if [ -d "$(pwd)/${p}/target/dependency" ]; then
      while IFS= read -r -d '' pdj; do CP_ELEMENTS+=("$pdj"); done < <(find "$(pwd)/${p}/target/dependency" -type f -name '*.jar' -print0)
    fi
  done

  IFS=$'\n' unique=($(awk '!x[$0]++' <<<"${CP_ELEMENTS[*]}"))
  CP=$(IFS=:; echo "${unique[*]}")
}

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
  run)
    discover_plugins
    modules_csv="${MAIN_MODULE_DIR}"
    for p in "${PLUGINS[@]}"; do modules_csv+=",${p}"; done
    echo "Building modules: ${modules_csv}"
    "${MAVEN}" -am -pl "${modules_csv}" -DskipTests clean install
    echo "Copying core runtime dependencies..."
    copy_core_dependencies
    echo "Copying plugin runtime dependencies..."
    copy_plugin_dependencies
    assemble_classpath
    run_main
    ;;
  *)
    usage
    ;;
esac

echo ""
echo "✓ 操作完成！"