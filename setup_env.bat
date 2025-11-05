@echo off
REM ==========================================
REM Team20 环境配置脚本 - Windows
REM 注意: 某些配置需要管理员权限
REM ==========================================

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo   Team20 环境配置 - Windows
echo ==========================================
echo.

REM 1. 检查 Java
echo 1. 检查 Java...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java 未安装
    echo.
    echo 请手动安装 Java 17:
    echo   1. 访问: https://adoptium.net/
    echo   2. 下载 Windows x64 安装包
    echo   3. 安装后配置 JAVA_HOME 环境变量
    echo.
    pause
    exit /b 1
) else (
    echo [OK] Java 已安装
)
echo.

REM 2. 检查 Git
echo 2. 检查 Git...
where git >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Git 未安装
    echo.
    echo 请手动安装 Git:
    echo   访问: https://git-scm.com/download/win
    echo.
    pause
    exit /b 1
) else (
    echo [OK] Git 已安装
)
echo.

REM 3. 配置 Git 行尾符
echo 3. 配置 Git...
git config --global core.autocrlf true
echo [OK] Git 行尾符配置完成 (autocrlf = true)

REM 检查用户信息
git config user.name >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARN] Git 用户信息未配置
    echo.
    set /p GIT_NAME="输入你的名字: "
    set /p GIT_EMAIL="输入你的邮箱: "
    git config --global user.name "!GIT_NAME!"
    git config --global user.email "!GIT_EMAIL!"
    echo [OK] Git 用户配置完成
)
echo.

REM 4. 创建 .editorconfig
echo 4. 创建配置文件...
if not exist ".editorconfig" (
    (
        echo root = true
        echo.
        echo [*]
        echo charset = utf-8
        echo end_of_line = lf
        echo insert_final_newline = true
        echo trim_trailing_whitespace = true
        echo.
        echo [*.java]
        echo indent_style = space
        echo indent_size = 4
        echo.
        echo [*.{xml,json,yml}]
        echo indent_style = space
        echo indent_size = 2
        echo.
        echo [*.{bat,cmd}]
        echo end_of_line = crlf
    ) > .editorconfig
    echo [OK] .editorconfig 创建完成
) else (
    echo [SKIP] .editorconfig 已存在
)
echo.

REM 5. 创建 .gitattributes
if not exist ".gitattributes" (
    (
        echo * text=auto
        echo *.java text eol=lf
        echo *.xml text eol=lf
        echo *.sh text eol=lf
        echo *.bat text eol=crlf
        echo *.cmd text eol=crlf
        echo *.jar binary
        echo mvnw text eol=lf
        echo mvnw.cmd text eol=crlf
    ) > .gitattributes
    echo [OK] .gitattributes 创建完成
) else (
    echo [SKIP] .gitattributes 已存在
)
echo.

REM 6. 生成 Maven Wrapper (如果有 Maven)
echo 5. 检查 Maven Wrapper...
if not exist "mvnw.cmd" (
    where mvn >nul 2>&1
    if %errorlevel% equ 0 (
        echo [INFO] 生成 Maven Wrapper...
        call mvn wrapper:wrapper -Dmaven=3.9.5
        echo [OK] Maven Wrapper 生成完成
    ) else (
        echo [WARN] Maven 未安装，无法生成 Wrapper
        echo   建议安装 Maven 后运行: mvn wrapper:wrapper
    )
) else (
    echo [OK] Maven Wrapper 已存在
)
echo.

REM 7. 总结
echo.
echo ==========================================
echo   配置完成！
echo ==========================================
echo.
echo [SUCCESS] 环境配置已完成
echo.
echo 下一步操作:
echo   1. 验证环境: verify_env.bat
echo   2. 编译项目: build.bat compile
echo   3. 开始开发！
echo.

REM 自动运行验证
if exist "verify_env.bat" (
    echo 按任意键运行环境验证...
    pause >nul
    call verify_env.bat
)
EOF
