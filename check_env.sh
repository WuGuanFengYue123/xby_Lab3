#!/bin/bash

# Team20 文本编辑器 - 环境检测脚本
# 使用方法: chmod +x check_env.sh && ./check_env.sh

echo "=========================================="
echo "  Team20 文本编辑器 - 环境检测"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查结果统计
PASS=0
FAIL=0
WARN=0

# 打印结果函数
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC} - $2"
        ((PASS++))
    elif [ $1 -eq 1 ]; then
        echo -e "${RED}✗ FAIL${NC} - $2"
        ((FAIL++))
    else
        echo -e "${YELLOW}⚠ WARN${NC} - $2"
        ((WARN++))
    fi
}

# 1. 检查操作系统
echo "1. 操作系统信息"
echo "----------------------------------------"
if [ -f /etc/os-release ]; then
    . /etc/os-release
    echo "   系统: $NAME"
    echo "   版本: $VERSION"
    print_result 0 "操作系统信息获取成功"
else
    echo "   系统: $(uname -s)"
    print_result 2 "无法获取详细系统信息"
fi
echo ""

# 2. 检查 Java/JDK
echo "2. Java 环境"
echo "----------------------------------------"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    echo "   Java 版本: $JAVA_VERSION"
    
    # 提取主版本号
    JAVA_MAJOR=$(echo $JAVA_VERSION | awk -F'[._]' '{print $1}')
    
    if [ "$JAVA_MAJOR" -ge 11 ]; then
        print_result 0 "Java 版本满足要求 (>= 11)"
        echo ""
        echo "   📝 建议 pom.xml 配置："
        echo "   <maven.compiler.source>$JAVA_MAJOR</maven.compiler.source>"
        echo "   <maven.compiler.target>$JAVA_MAJOR</maven.compiler.target>"
    elif [ "$JAVA_MAJOR" -ge 8 ]; then
        print_result 2 "Java 版本较低 (推荐 JDK 11+)"
        echo ""
        echo "   📝 pom.xml 配置："
        echo "   <maven.compiler.source>1.8</maven.compiler.source>"
        echo "   <maven.compiler.target>1.8</maven.compiler.target>"
    else
        print_result 1 "Java 版本过低 (需要 JDK 8+)"
    fi
    
    # 检查 JAVA_HOME
    if [ -n "$JAVA_HOME" ]; then
        echo "   JAVA_HOME: $JAVA_HOME"
        print_result 0 "JAVA_HOME 已设置"
    else
        print_result 2 "JAVA_HOME 未设置（建议设置）"
    fi
else
    print_result 1 "未安装 Java"
    echo ""
    echo "   安装命令："
    echo "   Ubuntu/Debian: sudo apt-get install openjdk-11-jdk"
    echo "   CentOS/RHEL:   sudo yum install java-11-openjdk-devel"
    echo "   macOS:         brew install openjdk@11"
fi
echo ""

# 3. 检查 javac (编译器)
echo "3. Java 编译器"
echo "----------------------------------------"
if command -v javac &> /dev/null; then
    JAVAC_VERSION=$(javac -version 2>&1 | awk '{print $2}')
    echo "   javac 版本: $JAVAC_VERSION"
    print_result 0 "Java 编译器可用"
else
    print_result 1 "未找到 javac（需要 JDK，不是 JRE）"
    echo ""
    echo "   提示: 你可能安装了 JRE 而不是 JDK"
    echo "   请安装完整的 JDK 包"
fi
echo ""

# 4. 检查 Maven
echo "4. Maven 构建工具"
echo "----------------------------------------"
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    echo "   Maven 版本: $MVN_VERSION"
    
    # 提取主版本号
    MVN_MAJOR=$(echo $MVN_VERSION | awk -F'.' '{print $1}')
    
    if [ "$MVN_MAJOR" -ge 3 ]; then
        print_result 0 "Maven 版本满足要求 (>= 3.6)"
    else
        print_result 2 "Maven 版本较低（建议 3.6+）"
    fi
    
    # 检查 Maven 配置
    if [ -f "$HOME/.m2/settings.xml" ]; then
        echo "   Maven 配置: $HOME/.m2/settings.xml"
        print_result 0 "Maven 配置文件存在"
    else
        print_result 2 "Maven 配置文件不存在（可选）"
        echo ""
        echo "   提示: 如果下载依赖慢，可以配置阿里云镜像"
        echo "   参考: QUICKSTART.md 中的 Q1"
    fi
else
    print_result 1 "未安装 Maven"
    echo ""
    echo "   安装命令："
    echo "   Ubuntu/Debian: sudo apt-get install maven"
    echo "   CentOS/RHEL:   sudo yum install maven"
    echo "   macOS:         brew install maven"
fi
echo ""

# 5. 检查 Git
echo "5. 版本控制"
echo "----------------------------------------"
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | awk '{print $3}')
    echo "   Git 版本: $GIT_VERSION"
    print_result 0 "Git 已安装"
    
    # 检查 Git 配置
    if git config --global user.name &> /dev/null; then
        GIT_USER=$(git config --global user.name)
        GIT_EMAIL=$(git config --global user.email)
        echo "   Git 用户: $GIT_USER <$GIT_EMAIL>"
        print_result 0 "Git 已配置"
    else
        print_result 2 "Git 用户信息未配置"
        echo ""
        echo "   配置命令："
        echo "   git config --global user.name \"Your Name\""
        echo "   git config --global user.email \"your.email@example.com\""
    fi
else
    print_result 1 "未安装 Git"
fi
echo ""

# 6. 检查必要的命令行工具
echo "6. 其他工具"
echo "----------------------------------------"

# tree 命令
if command -v tree &> /dev/null; then
    print_result 0 "tree 命令可用"
else
    print_result 2 "tree 命令未安装（推荐）"
    echo "   安装: sudo apt-get install tree"
fi

# curl 或 wget
if command -v curl &> /dev/null; then
    print_result 0 "curl 命令可用"
elif command -v wget &> /dev/null; then
    print_result 0 "wget 命令可用"
else
    print_result 2 "curl/wget 未安装（Maven 需要）"
fi

echo ""

# 7. 检查磁盘空间
echo "7. 磁盘空间"
echo "----------------------------------------"
AVAILABLE_SPACE=$(df -h . | tail -1 | awk '{print $4}')
echo "   可用空间: $AVAILABLE_SPACE"
print_result 0 "磁盘空间检查完成"
echo ""

# 8. 检查内存
echo "8. 内存信息"
echo "----------------------------------------"
if command -v free &> /dev/null; then
    TOTAL_MEM=$(free -h | grep Mem | awk '{print $2}')
    AVAIL_MEM=$(free -h | grep Mem | awk '{print $7}')
    echo "   总内存: $TOTAL_MEM"
    echo "   可用内存: $AVAIL_MEM"
    print_result 0 "内存信息获取成功"
else
    print_result 2 "无法获取内存信息"
fi
echo ""

# 9. 检查项目文件
echo "9. 项目文件检查"
echo "----------------------------------------"
if [ -f "pom.xml" ]; then
    print_result 0 "pom.xml 存在"
else
    print_result 2 "pom.xml 不存在（需要创建）"
fi

if [ -d "src" ]; then
    print_result 0 "src 目录存在"
else
    print_result 2 "src 目录不存在（需要运行 setup_project.sh）"
fi

if [ -f ".gitignore" ]; then
    print_result 0 ".gitignore 存在"
else
    print_result 2 ".gitignore 不存在"
fi
echo ""

# 10. 检查 DevContainer (可选)
echo "10. DevContainer 检查"
echo "----------------------------------------"
if [ -d ".devcontainer" ]; then
    print_result 0 ".devcontainer 目录存在"
    
    if [ -f ".devcontainer/devcontainer.json" ]; then
        echo "   配置文件: devcontainer.json 存在"
        
        # 尝试读取 JDK 版本
        if [ -f ".devcontainer/Dockerfile" ]; then
            DOCKER_JAVA=$(grep -i "FROM.*openjdk" .devcontainer/Dockerfile | head -1)
            if [ -n "$DOCKER_JAVA" ]; then
                echo "   Docker JDK: $DOCKER_JAVA"
            fi
        fi
    fi
else
    print_result 2 "未使用 DevContainer（可选）"
fi
echo ""

# 总结
echo "=========================================="
echo "  检测总结"
echo "=========================================="
echo -e "${GREEN}通过: $PASS${NC}"
echo -e "${YELLOW}警告: $WARN${NC}"
echo -e "${RED}失败: $FAIL${NC}"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}✓ 环境检测通过！可以开始开发。${NC}"
    echo ""
    echo "下一步操作："
    echo "1. 运行 ./setup_project.sh 生成项目框架"
    echo "2. 执行 mvn clean compile 验证 Maven 配置"
    echo "3. 开始编写代码！"
else
    echo -e "${RED}✗ 环境存在问题，请先解决上述失败项。${NC}"
fi

echo ""
echo "详细指南: 查看 QUICKSTART.md"
echo "=========================================="
