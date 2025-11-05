#!/bin/bash

##############################################
# Team20 环境自动配置脚本
# 自动检测并配置开发环境
##############################################

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }

# 检测平台
case "$(uname -s)" in
    Linux*)
        if grep -qi microsoft /proc/version 2>/dev/null; then
            PLATFORM="WSL"
        else
            PLATFORM="Linux"
        fi
        ;;
    Darwin*) PLATFORM="macOS";;
    *) PLATFORM="Unknown";;
esac

echo ""
echo "=========================================="
echo "  Team20 环境配置 - $PLATFORM"
echo "=========================================="
echo ""

##############################################
# 1. 配置 Git
##############################################
print_info "配置 Git..."

# 行尾符配置
if [ "$PLATFORM" == "WSL" ] || [ "$PLATFORM" == "Linux" ] || [ "$PLATFORM" == "macOS" ]; then
    git config --global core.autocrlf input
    print_success "Git 行尾符配置: input"
fi

# 检查用户配置
if ! git config user.name &> /dev/null; then
    print_warning "Git 用户信息未配置"
    read -p "输入你的名字: " GIT_NAME
    read -p "输入你的邮箱: " GIT_EMAIL
    git config --global user.name "$GIT_NAME"
    git config --global user.email "$GIT_EMAIL"
    print_success "Git 用户配置完成"
fi

echo ""

##############################################
# 2. 创建 .editorconfig
##############################################
print_info "创建 .editorconfig..."

if [ ! -f ".editorconfig" ]; then
cat > .editorconfig << 'EDITORCONFIG'
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.java]
indent_style = space
indent_size = 4
max_line_length = 120

[*.{xml,json,yml,yaml}]
indent_style = space
indent_size = 2

[*.md]
trim_trailing_whitespace = false

[*.{sh,bash}]
indent_style = space
indent_size = 2
end_of_line = lf

[*.{bat,cmd}]
indent_style = space
indent_size = 2
end_of_line = crlf
EDITORCONFIG
    print_success ".editorconfig 创建完成"
else
    print_warning ".editorconfig 已存在，跳过"
fi

echo ""

##############################################
# 3. 创建 .gitattributes
##############################################
print_info "创建 .gitattributes..."

if [ ! -f ".gitattributes" ]; then
cat > .gitattributes << 'GITATTRIBUTES'
# 自动检测并标准化行尾符
* text=auto

# 源代码使用 LF
*.java text eol=lf
*.xml text eol=lf
*.properties text eol=lf
*.md text eol=lf
*.sh text eol=lf
*.json text eol=lf
*.yml text eol=lf
*.yaml text eol=lf

# Windows 脚本使用 CRLF
*.bat text eol=crlf
*.cmd text eol=crlf

# 二进制文件
*.jar binary
*.class binary
*.png binary
*.jpg binary

# Maven Wrapper
mvnw text eol=lf
mvnw.cmd text eol=crlf
GITATTRIBUTES
    print_success ".gitattributes 创建完成"
else
    print_warning ".gitattributes 已存在，跳过"
fi

echo ""

##############################################
# 4. 生成 Maven Wrapper（如果不存在）
##############################################
print_info "检查 Maven Wrapper..."

if [ ! -f "mvnw" ]; then
    print_warning "Maven Wrapper 不存在"
    
    if command -v mvn &> /dev/null; then
        print_info "使用系统 Maven 生成 Wrapper..."
        mvn wrapper:wrapper -Dmaven=3.9.5
        chmod +x mvnw
        print_success "Maven Wrapper 生成完成"
    else
        print_warning "Maven 未安装，跳过 Wrapper 生成"
        echo "  手动生成: mvn wrapper:wrapper -Dmaven=3.9.5"
    fi
else
    print_success "Maven Wrapper 已存在"
    chmod +x mvnw 2>/dev/null || true
fi

echo ""

##############################################
# 5. 配置 VS Code（如果存在）
##############################################
if command -v code &> /dev/null || [ -d ".vscode" ]; then
    print_info "配置 VS Code..."
    
    mkdir -p .vscode
    
    # settings.json
    if [ ! -f ".vscode/settings.json" ]; then
cat > .vscode/settings.json << 'VSCODE_SETTINGS'
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "maven.executable.preferMavenWrapper": true,
  "files.encoding": "utf8",
  "files.eol": "\n",
  "editor.formatOnSave": true,
  "editor.tabSize": 4,
  "editor.insertSpaces": true,
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.tabSize": 4
  },
  "[xml]": {
    "editor.tabSize": 2
  },
  "[json]": {
    "editor.tabSize": 2
  }
}
VSCODE_SETTINGS
        print_success ".vscode/settings.json 创建完成"
    fi
fi

echo ""

##############################################
# 6. 标准化现有文件行尾符
##############################################
print_info "标准化文件行尾符..."

if command -v dos2unix &> /dev/null; then
    find src -name "*.java" -exec dos2unix {} + 2>/dev/null || true
    dos2unix mvnw 2>/dev/null || true
    print_success "行尾符标准化完成"
elif command -v sed &> /dev/null; then
    find src -name "*.java" -exec sed -i 's/\r$//' {} + 2>/dev/null || true
    sed -i 's/\r$//' mvnw 2>/dev/null || true
    print_success "行尾符标准化完成（使用 sed）"
else
    print_warning "dos2unix 和 sed 都不可用，跳过行尾符转换"
fi

echo ""

##############################################
# 7. 设置 JAVA_HOME（如果未设置）
##############################################
if [ -z "$JAVA_HOME" ]; then
    print_info "配置 JAVA_HOME..."
    
    case "$PLATFORM" in
        "macOS")
            JAVA_HOME_PATH=$(/usr/libexec/java_home -v 17 2>/dev/null || echo "")
            if [ -n "$JAVA_HOME_PATH" ]; then
                echo "export JAVA_HOME=$JAVA_HOME_PATH" >> ~/.zshrc
                print_success "JAVA_HOME 已添加到 ~/.zshrc"
                print_warning "请运行: source ~/.zshrc"
            fi
            ;;
        "Linux"|"WSL")
            if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
                echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64" >> ~/.bashrc
                print_success "JAVA_HOME 已添加到 ~/.bashrc"
                print_warning "请运行: source ~/.bashrc"
            fi
            ;;
    esac
fi

echo ""

##############################################
# 总结
##############################################
echo "=========================================="
echo "  配置完成！"
echo "=========================================="
echo ""
print_success "环境配置已完成"
echo ""
echo "下一步操作:"
echo "  1. 运行验证: ./verify_env.sh"
echo "  2. 编译项目: ./mvnw clean compile"
echo "  3. 开始开发！"
echo ""

# 运行验证
if [ -f "./verify_env.sh" ]; then
    print_info "自动运行环境验证..."
    echo ""
    ./verify_env.sh
fi
