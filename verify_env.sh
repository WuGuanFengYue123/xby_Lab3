#!/bin/bash

##############################################
# Team20 全平台环境验证脚本
# 支持: Linux, macOS, WSL, Git Bash, Docker
##############################################

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

print_header() {
    echo ""
    echo "=========================================="
    echo "  $1"
    echo "=========================================="
    echo ""
}

print_success() { echo -e "${GREEN}✓${NC} $1"; }
print_warning() { echo -e "${YELLOW}!${NC} $1"; }
print_error() { echo -e "${RED}✗${NC} $1"; }
print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }

# 检测平台
detect_platform() {
    case "$(uname -s)" in
        Linux*)
            if grep -qi microsoft /proc/version 2>/dev/null; then
                PLATFORM="WSL"
            elif [ -f /.dockerenv ]; then
                PLATFORM="Docker"
            else
                PLATFORM="Linux"
            fi
            ;;
        Darwin*)    PLATFORM="macOS";;
        CYGWIN*)    PLATFORM="Cygwin";;
        MINGW*)     PLATFORM="Git Bash (Windows)";;
        MSYS*)      PLATFORM="MSYS (Windows)";;
        *)          PLATFORM="Unknown";;
    esac
}

detect_platform
print_header "Team20 环境验证 - $PLATFORM"

ERRORS=0
WARNINGS=0

##############################################
# 1. Java 版本检查
##############################################
echo "1. Java 环境检查"
echo "----------------------------------------"

if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d'.' -f1)
    
    if [ "$JAVA_MAJOR" -ge 17 ]; then
        print_success "Java $JAVA_VERSION"
    else
        print_error "Java 版本过低: $JAVA_VERSION (需要 >= 17)"
        ((ERRORS++))
    fi
    
    if [ -n "$JAVA_HOME" ]; then
        print_success "JAVA_HOME: $JAVA_HOME"
    else
        print_warning "JAVA_HOME 未设置"
        echo "  建议设置（添加到 ~/.bashrc 或 ~/.zshrc）:"
        case "$PLATFORM" in
            "macOS")
                echo "  export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
                ;;
            "Linux"|"WSL")
                echo "  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
                ;;
        esac
        ((WARNINGS++))
    fi
else
    print_error "Java 未安装"
    echo ""
    echo "安装指南:"
    case "$PLATFORM" in
        "macOS")
            echo "  # 使用 Homebrew"
            echo "  brew install openjdk@17"
            echo "  # 或下载 Adoptium"
            echo "  https://adoptium.net/"
            ;;
        "Linux"|"WSL")
            echo "  # Debian/Ubuntu"
            echo "  sudo apt update && sudo apt install -y openjdk-17-jdk"
            echo "  # RHEL/CentOS/Fedora"
            echo "  sudo dnf install -y java-17-openjdk-devel"
            ;;
        "Git Bash (Windows)"|"MSYS (Windows)")
            echo "  # 下载安装包"
            echo "  https://adoptium.net/"
            echo "  # 或使用 Scoop"
            echo "  scoop install openjdk17"
            ;;
    esac
    ((ERRORS++))
fi
echo ""

##############################################
# 2. Maven 检查
##############################################
echo "2. Maven 环境检查"
echo "----------------------------------------"

if [ -f "mvnw" ]; then
    print_success "Maven Wrapper 已配置"
    
    # 检查权限
    if [ -x "mvnw" ]; then
        print_success "mvnw 权限正确"
    else
        print_warning "mvnw 不可执行，正在修复..."
        chmod +x mvnw
        print_success "已修复 mvnw 权限"
    fi
    
    MAVEN_CMD="./mvnw"
    
    # 测试 Maven Wrapper
    if $MAVEN_CMD --version &> /dev/null; then
        MVN_VERSION=$($MAVEN_CMD --version | head -n1 | awk '{print $3}')
        print_success "Maven 版本: $MVN_VERSION (via Wrapper)"
    else
        print_warning "Maven Wrapper 不可用，回退到系统 Maven"
        MAVEN_CMD="mvn"
    fi
elif command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn --version | head -n1 | awk '{print $3}')
    print_success "系统 Maven: $MVN_VERSION"
    MAVEN_CMD="mvn"
    
    print_warning "未检测到 Maven Wrapper（推荐）"
    echo "  生成命令: mvn wrapper:wrapper -Dmaven=3.9.5"
    ((WARNINGS++))
else
    print_error "Maven 未安装且无 Maven Wrapper"
    echo ""
    echo "安装指南:"
    case "$PLATFORM" in
        "macOS")
            echo "  brew install maven"
            ;;
        "Linux"|"WSL")
            echo "  # Debian/Ubuntu"
            echo "  sudo apt install -y maven"
            echo "  # RHEL/CentOS/Fedora"
            echo "  sudo dnf install -y maven"
            ;;
        "Git Bash (Windows)"|"MSYS (Windows)")
            echo "  # 下载并配置"
            echo "  https://maven.apache.org/download.cgi"
            echo "  # 或使用 Scoop"
            echo "  scoop install maven"
            ;;
    esac
    echo ""
    echo "或生成 Maven Wrapper（推荐，无需安装 Maven）:"
    echo "  curl -o mvnw https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw"
    echo "  curl -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd"
    ((ERRORS++))
fi
echo ""

##############################################
# 3. Git 配置检查
##############################################
echo "3. Git 配置检查"
echo "----------------------------------------"

if command -v git &> /dev/null; then
    print_success "Git 已安装: $(git --version | awk '{print $3}')"
    
    # 检查用户配置
    GIT_USER=$(git config user.name 2>/dev/null || echo "")
    GIT_EMAIL=$(git config user.email 2>/dev/null || echo "")
    
    if [ -n "$GIT_USER" ] && [ -n "$GIT_EMAIL" ]; then
        print_success "Git 用户: $GIT_USER <$GIT_EMAIL>"
    else
        print_warning "Git 用户信息未配置"
        echo "  git config --global user.name \"Your Name\""
        echo "  git config --global user.email \"your@email.com\""
        ((WARNINGS++))
    fi
    
    # 检查行尾符配置（跨平台关键）
    AUTOCRLF=$(git config core.autocrlf 2>/dev/null || echo "未设置")
    case "$PLATFORM" in
        "Git Bash (Windows)"|"MSYS (Windows)"|"Cygwin")
            if [ "$AUTOCRLF" != "true" ]; then
                print_warning "Windows 建议设置:"
                echo "  git config --global core.autocrlf true"
                ((WARNINGS++))
            else
                print_success "行尾符配置正确 (autocrlf = true)"
            fi
            ;;
        *)
            if [ "$AUTOCRLF" != "input" ]; then
                print_warning "Linux/Mac/WSL 建议设置:"
                echo "  git config --global core.autocrlf input"
                ((WARNINGS++))
            else
                print_success "行尾符配置正确 (autocrlf = input)"
            fi
            ;;
    esac
else
    print_error "Git 未安装"
    ((ERRORS++))
fi
echo ""

##############################################
# 4. 项目文件检查
##############################################
echo "4. 项目文件检查"
echo "----------------------------------------"

# pom.xml
if [ -f "pom.xml" ]; then
    print_success "pom.xml 存在"
    
    if [ -n "$MAVEN_CMD" ]; then
        if $MAVEN_CMD validate &> /dev/null 2>&1; then
            print_success "pom.xml 验证通过"
        else
            print_error "pom.xml 验证失败"
            echo "  运行查看详情: $MAVEN_CMD validate"
            ((ERRORS++))
        fi
    fi
else
    print_error "pom.xml 不存在"
    ((ERRORS++))
fi

# .gitignore
[ -f ".gitignore" ] && print_success ".gitignore 存在" || { print_warning ".gitignore 不存在"; ((WARNINGS++)); }

# .editorconfig
if [ -f ".editorconfig" ]; then
    print_success ".editorconfig 存在"
else
    print_warning ".editorconfig 不存在（推荐创建）"
    ((WARNINGS++))
fi

# 项目结构
if [ -d "src/main/java" ]; then
    JAVA_FILES=$(find src/main/java -name "*.java" 2>/dev/null | wc -l)
    print_success "项目结构存在 ($JAVA_FILES 个 Java 文件)"
else
    print_error "项目结构不完整"
    ((ERRORS++))
fi
echo ""

##############################################
# 5. 文件编码检查
##############################################
echo "5. 文件编码检查"
echo "----------------------------------------"

if command -v file &> /dev/null; then
    NON_UTF8=0
    SAMPLE_FILES=$(find src/main/java -name "*.java" 2>/dev/null | head -5)
    
    for f in $SAMPLE_FILES; do
        ENCODING=$(file -b --mime-encoding "$f" 2>/dev/null || echo "unknown")
        if [ "$ENCODING" != "utf-8" ] && [ "$ENCODING" != "us-ascii" ]; then
            print_error "$f: $ENCODING (应为 UTF-8)"
            ((NON_UTF8++))
        fi
    done
    
    if [ $NON_UTF8 -eq 0 ]; then
        print_success "文件编码正确 (UTF-8)"
    else
        print_error "发现 $NON_UTF8 个非 UTF-8 文件"
        echo "  转换命令: find src -name '*.java' -exec sh -c 'iconv -f GBK -t UTF-8 \"\$1\" > \"\$1.tmp\" && mv \"\$1.tmp\" \"\$1\"' _ {} \;"
        ((ERRORS++))
    fi
else
    print_warning "无法检查文件编码 (file 命令不可用)"
fi
echo ""

##############################################
# 6. 平台特定检查
##############################################
echo "6. 平台特定检查"
echo "----------------------------------------"

case "$PLATFORM" in
    "WSL")
        print_info "WSL 环境"
        if command -v cmd.exe &> /dev/null; then
            print_success "可访问 Windows 命令"
        fi
        # 检查 Windows 路径
        if [ -d "/mnt/c" ]; then
            print_success "Windows 文件系统已挂载"
        fi
        ;;
    "Docker")
        print_info "Docker 容器环境"
        if [ -d "/home/vscode/.m2" ]; then
            print_success "Maven 缓存目录已挂载"
        fi
        if [ -f "/.dockerenv" ]; then
            print_success "Docker 环境确认"
        fi
        ;;
    "macOS")
        print_info "macOS 环境"
        if command -v brew &> /dev/null; then
            print_success "Homebrew 已安装"
        else
            print_warning "Homebrew 未安装（推荐）"
            echo "  安装: /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
        fi
        ;;
    "Git Bash (Windows)")
        print_info "Git Bash (Windows) 环境"
        print_warning "建议使用 WSL 或 PowerShell + mvnw.cmd"
        ;;
esac
echo ""

##############################################
# 总结
##############################################
print_header "验证结果"

echo "平台: $PLATFORM"
echo "错误: $ERRORS"
echo "警告: $WARNINGS"
echo ""

if [ $ERRORS -eq 0 ]; then
    print_success "环境验证通过！"
    echo ""
    echo "下一步操作:"
    if [ -n "$MAVEN_CMD" ]; then
        echo "  1. 编译项目: $MAVEN_CMD clean compile"
        echo "  2. 运行测试: $MAVEN_CMD test"
        echo "  3. 打包项目: $MAVEN_CMD package"
        echo "  4. 运行程序: $MAVEN_CMD exec:java -Dexec.mainClass=\"com.team20.editor.Main\""
    fi
    echo ""
    exit 0
else
    print_error "发现 $ERRORS 个错误，请修复后继续"
    echo ""
    exit 1
fi
EOF
