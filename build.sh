#!/bin/bash
##############################################
# Team20 统一构建脚本
##############################################

set -e

# 自动检测 Maven
if [ -f "mvnw" ]; then
    MAVEN="./mvnw"
else
    MAVEN="mvn"
fi

case "$1" in
    compile|build)
        echo "编译项目..."
        $MAVEN clean compile
        ;;
    test)
        echo "运行测试..."
        $MAVEN test
        ;;
    package)
        echo "打包项目..."
        $MAVEN clean package
        ;;
    run)
        echo "运行程序..."
        $MAVEN exec:java -Dexec.mainClass="com.team20.editor.Main"
        ;;
    clean)
        echo "清理项目..."
        $MAVEN clean
        ;;
    verify)
        echo "完整验证（编译+测试+打包）..."
        $MAVEN clean verify
        ;;
    *)
        echo "用法: $0 {compile|test|package|run|clean|verify}"
        echo ""
        echo "命令说明:"
        echo "  compile  - 编译项目"
        echo "  test     - 运行测试"
        echo "  package  - 打包为 JAR"
        echo "  run      - 运行主程序"
        echo "  clean    - 清理构建文件"
        echo "  verify   - 完整验证（编译+测试+打包）"
        echo ""
        echo "示例:"
        echo "  $0 compile    # 编译"
        echo "  $0 run        # 运行"
        echo "  $0 package    # 打包"
        exit 1
        ;;
esac

echo ""
echo "✓ 操作完成！"
