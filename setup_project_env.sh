#!/usr/bin/env bash
#
# setup_project_env.sh
# 统一初始化/校正项目的 SPI (Provider) 结构 + 基础环境验证
# 可重复执行，幂等安全。
#
# 用法：
#   bash setup_project_env.sh            # 默认模式
#   bash setup_project_env.sh --no-build # 只修复结构不编译
#   bash setup_project_env.sh --dry-run  # 仅打印将执行的操作
#
set -euo pipefail

DRY_RUN=false
NO_BUILD=false

for arg in "$@"; do
  case "$arg" in
    --dry-run) DRY_RUN=true ;;
    --no-build) NO_BUILD=true ;;
    -h|--help)
      echo "Usage: $0 [--dry-run] [--no-build]"
      exit 0
      ;;
    *) echo "[WARN] Unknown argument: $arg" ;;
  esac
done

run() {
  if $DRY_RUN; then
    printf "[DRY-RUN] %s\n" "$*"
  else
    eval "$@"
  fi
}

echo "=============================================="
echo " Team20 Text Editor - Project SPI Environment "
echo "=============================================="
echo ""

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# 若脚本不在根目录，可调整为：PROJECT_ROOT=$(git rev-parse --show-toplevel)

echo "[INFO] Project root: $PROJECT_ROOT"

SRC_MAIN="src/main/java/com/team20/editor"
RES_MAIN="src/main/resources/META-INF/services"

# 1. 校验必要目录
NEEDED_DIRS=(
  "$SRC_MAIN/extension/spi/command"
  "$SRC_MAIN/extension/spi/editor"
  "$SRC_MAIN/extension/spi/serialization"
  "$SRC_MAIN/extension/spi/node"
  "$RES_MAIN"
)

echo "[STEP] 检查并创建 SPI 目录"
for d in "${NEEDED_DIRS[@]}"; do
  if [ ! -d "$d" ]; then
    run "mkdir -p $d"
    echo "  [+] 创建目录: $d"
  else
    echo "  [✓] 已存在: $d"
  fi
done
echo ""

# 2. 确保 SPI 接口存在（如果误删则自动补全）
create_file_if_missing() {
  local path="$1"
  local content="$2"
  if [ ! -f "$path" ]; then
    run "cat > $path <<'EOF_SPI'\n${content}\nEOF_SPI"
    echo "  [+] 补全接口文件: $path"
  else
    echo "  [✓] 接口存在: $path"
  fi
}

echo "[STEP] 校验 SPI 接口文件"

# CommandProvider
create_file_if_missing "$SRC_MAIN/extension/spi/command/CommandProvider.java" \
'package com.team20.editor.extension.spi.command;
import com.team20.editor.domain.command.CommandDescriptor;
import java.util.Collection;
/** SPI: 命令提供者 */ 
public interface CommandProvider {
    Collection<CommandDescriptor> descriptors();
}'

# EditorProvider
create_file_if_missing "$SRC_MAIN/extension/spi/editor/EditorProvider.java" \
'package com.team20.editor.extension.spi.editor;
import com.team20.editor.domain.editor.Editor;
import java.util.Collection;
/** SPI: 编辑器类型提供者 */ 
public interface EditorProvider {
    Collection<EditorRegistration> editors();
    record EditorRegistration(String type, Factory factory, String description) {
        public interface Factory { Editor create(); }
        public static EditorRegistration of(String type, Factory factory, String description) {
            return new EditorRegistration(type, factory, description);
        }
    }
}'

# SerializerProvider
create_file_if_missing "$SRC_MAIN/extension/spi/serialization/SerializerProvider.java" \
'package com.team20.editor.extension.spi.serialization;
import com.team20.editor.infrastructure.persistence.Serializer;
import java.util.Collection;
/** SPI: 序列化格式提供者 */ 
public interface SerializerProvider {
    Collection<Serializer> serializers();
}'

# NodeAdapterProvider
create_file_if_missing "$SRC_MAIN/extension/spi/node/NodeAdapterProvider.java" \
'package com.team20.editor.extension.spi.node;
import com.team20.editor.representation.tree.NodeAdapterFactory;
import java.util.Collection;
/** SPI: 树节点适配工厂提供者 */ 
public interface NodeAdapterProvider {
    Collection<NodeAdapterFactory> factories();
}'

echo ""

# 3. 资源文件旧残留清理（如果还在使用旧命名）
echo "[STEP] 清理旧 ServiceLoader 资源文件（如果存在）"
LEGACY_FILES=(
  "$RES_MAIN/com.team20.editor.core.command.CommandProvider"
  "$RES_MAIN/com.team20.editor.persistence.SerializerProvider"
  "$RES_MAIN/com.team20.editor.registry.EditorProvider"
  "$RES_MAIN/com.team20.editor.tree.NodeAdapterProvider"
)
for f in "${LEGACY_FILES[@]}"; do
  if [ -f "$f" ]; then
    run "rm -f $f"
    echo "  [-] 删除旧文件: $f"
  fi
done
echo "  [✓] 清理完成"
echo ""

# 4. 重建新 ServiceLoader 文件（幂等）
echo "[STEP] 重建 META-INF/services 资源"

# 这里从现有实现类推断，你的实现类尚在旧包；无需迁移实现，只需列出全限定名
run "cat > $RES_MAIN/com.team20.editor.extension.spi.command.CommandProvider <<'EOF_CMD'
com.team20.editor.domain.command.impl.logging.LoggingCommandProvider
EOF_CMD"

run "cat > $RES_MAIN/com.team20.editor.extension.spi.editor.EditorProvider <<'EOF_ED'
com.team20.editor.domain.editor.text.TextEditorProvider
EOF_ED"

run "cat > $RES_MAIN/com.team20.editor.extension.spi.serialization.SerializerProvider <<'EOF_SER'
com.team20.editor.infrastructure.persistence.DefaultSerializerProvider
EOF_SER"

run "cat > $RES_MAIN/com.team20.editor.extension.spi.node.NodeAdapterProvider <<'EOF_NODE'
com.team20.editor.representation.tree.providers.CoreNodeAdapterProvider
EOF_NODE"

echo "  [✓] 新资源文件已写入"
echo ""

# 5. 行尾 & 执行权限校验（仅对脚本和 mvnw）
echo "[STEP] 设置脚本执行权限与行尾标准化"

SCRIPT_FILES=(
  "setup_project_env.sh"
  "setup_env.sh"
  "verify_env.sh"
  "build.sh"
  "mvnw"
)

for sf in "${SCRIPT_FILES[@]}"; do
  if [ -f "$sf" ]; then
    run "chmod +x $sf"
    # 替换 CRLF 为 LF（不直接写 sed -i 's/\r$//'，使用 dos2unix 若存在）
    if command -v dos2unix >/dev/null 2>&1; then
      run "dos2unix $sf"
    else
      run "sed -i 's/\r$//' $sf"
    fi
    echo "  [✓] 规范: $sf"
  fi
done
echo ""

# 6. 编译 & 简单运行（可跳过）
if ! $NO_BUILD; then
  echo "[STEP] Maven 编译验证"
  run "./mvnw -q clean compile"
  echo "  [✓] 编译成功"
  echo ""

  echo "[STEP] ServiceLoader Smoke 测试 (反射加载数量)"
  # 使用临时 Java 代码快速验证（不引入新类）
  TMP_CLASS=$(mktemp /tmp/ServiceLoaderTestXXXX.java)
  cat > "$TMP_CLASS" <<'EOF_JAVA'
import java.util.ServiceLoader;
public class ServiceLoaderTest {
  public static void main(String[] args) {
    int command = ServiceLoader.load(com.team20.editor.extension.spi.command.CommandProvider.class).stream().toList().size();
    int editor = ServiceLoader.load(com.team20.editor.extension.spi.editor.EditorProvider.class).stream().toList().size();
    int ser = ServiceLoader.load(com.team20.editor.extension.spi.serialization.SerializerProvider.class).stream().toList().size();
    int node = ServiceLoader.load(com.team20.editor.extension.spi.node.NodeAdapterProvider.class).stream().toList().size();
    System.out.println("Providers => command=" + command + ", editor=" + editor + ", serializer=" + ser + ", node=" + node);
    if (command == 0 || editor == 0 || ser == 0 || node == 0) {
      System.err.println("ERROR: Missing provider(s).");
      System.exit(1);
    }
  }
}
EOF_JAVA

  # 编译并运行
  run "javac -cp target/classes $TMP_CLASS"
  run "java -cp target/classes:/tmp ServiceLoaderTest"
  echo "  [✓] ServiceLoader 正常工作"
  rm -f "$TMP_CLASS"
  echo ""
else
  echo "[INFO] 跳过构建 (--no-build)"
fi

# 7. 汇总
echo "=============================================="
echo "  完成：SPI 结构/资源统一"
echo "  DRY_RUN: $DRY_RUN"
echo "  NO_BUILD: $NO_BUILD"
echo "  资源文件列表:"
ls -1 "$RES_MAIN" | sed 's/^/    - /'
echo "=============================================="
echo ""
echo "[下一步建议]"
echo "  1. 在 README 的扩展点章节更新为新的接口包名。"
echo "  2. 新增扩展时：实现类放原有实现包，资源文件追加行即可。"
echo "  3. 可添加 ExtensionDiscovery 测试确保所有 Provider 都被加载。"
echo ""
echo "Done."
