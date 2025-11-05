@echo off
REM ==========================================
REM Team20 环境验证脚本 - Windows 原生
REM ==========================================

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo   Team20 环境验证 - Windows
echo ==========================================
echo.

set ERRORS=0
set WARNINGS=0

REM 1. Java 检查
echo 1. Java 环境检查
echo ----------------------------------------

where java >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Java 已安装
    for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /C:"version"') do (
        set JAVA_VER=%%i
        set JAVA_VER=!JAVA_VER:"=!
    )
    echo     版本: !JAVA_VER!
    
    if defined JAVA_HOME (
        echo [OK] JAVA_HOME: %JAVA_HOME%
    ) else (
        echo [WARN] JAVA_HOME 未设置
        echo   建议设置系统环境变量 JAVA_HOME
        set /a WARNINGS+=1
    )
) else (
    echo [ERROR] Java 未安装
    echo.
    echo 安装指南:
    echo   1. 下载 Adoptium OpenJDK 17: https://adoptium.net/
    echo   2. 或使用 Scoop: scoop install openjdk17
    set /a ERRORS+=1
)
echo.

REM 2. Maven 检查
echo 2. Maven 环境检查
echo ----------------------------------------

if exist "mvnw.cmd" (
    echo [OK] Maven Wrapper 已配置
    set MAVEN_CMD=mvnw.cmd
    call mvnw.cmd --version >nul 2>&1
    if %errorlevel% equ 0 (
        for /f "tokens=3" %%i in ('mvnw.cmd --version 2^>^&1 ^| findstr /C:"Apache Maven"') do (
            echo [OK] Maven 版本: %%i (via Wrapper)
        )
    )
) else (
    where mvn >nul 2>&1
    if %errorlevel% equ 0 (
        echo [OK] 系统 Maven 已安装
        for /f "tokens=3" %%i in ('mvn --version 2^>^&1 ^| findstr /C:"Apache Maven"') do (
            echo     版本: %%i
        )
        set MAVEN_CMD=mvn
        echo [WARN] 建议生成 Maven Wrapper
        echo   mvn wrapper:wrapper -Dmaven=3.9.5
        set /a WARNINGS+=1
    ) else (
        echo [ERROR] Maven 未安装且无 Maven Wrapper
        echo.
        echo 安装指南:
        echo   1. 下载: https://maven.apache.org/download.cgi
        echo   2. 或使用 Scoop: scoop install maven
        set /a ERRORS+=1
    )
)
echo.

REM 3. Git 检查
echo 3. Git 配置检查
echo ----------------------------------------

where git >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Git 已安装
    for /f "tokens=3" %%i in ('git --version') do echo     版本: %%i
    
    git config user.name >nul 2>&1
    if %errorlevel% equ 0 (
        for /f "delims=" %%i in ('git config user.name') do (
            echo [OK] Git 用户: %%i
        )
    ) else (
        echo [WARN] Git 用户信息未配置
        echo   git config --global user.name "Your Name"
        echo   git config --global user.email "your@email.com"
        set /a WARNINGS+=1
    )
    
    for /f "tokens=*" %%i in ('git config core.autocrlf 2^>nul') do set AUTOCRLF=%%i
    if "!AUTOCRLF!"=="true" (
        echo [OK] 行尾符配置正确 (autocrlf = true)
    ) else (
        echo [WARN] Windows 建议设置:
        echo   git config --global core.autocrlf true
        set /a WARNINGS+=1
    )
) else (
    echo [ERROR] Git 未安装
    echo   下载: https://git-scm.com/download/win
    set /a ERRORS+=1
)
echo.

REM 4. 项目文件检查
echo 4. 项目文件检查
echo ----------------------------------------

if exist "pom.xml" (
    echo [OK] pom.xml 存在
    
    if defined MAVEN_CMD (
        !MAVEN_CMD! validate >nul 2>&1
        if %errorlevel% equ 0 (
            echo [OK] pom.xml 验证通过
        ) else (
            echo [ERROR] pom.xml 验证失败
            echo   运行查看: !MAVEN_CMD! validate
            set /a ERRORS+=1
        )
    )
) else (
    echo [ERROR] pom.xml 不存在
    set /a ERRORS+=1
)

if exist ".gitignore" (
    echo [OK] .gitignore 存在
) else (
    echo [WARN] .gitignore 不存在
    set /a WARNINGS+=1
)

if exist ".editorconfig" (
    echo [OK] .editorconfig 存在
) else (
    echo [WARN] .editorconfig 不存在（推荐）
    set /a WARNINGS+=1
)

if exist "src\main\java" (
    echo [OK] 项目结构存在
) else (
    echo [ERROR] 项目结构不完整
    set /a ERRORS+=1
)
echo.

REM 5. PowerShell 检查（可选）
echo 5. 开发工具检查
echo ----------------------------------------

where powershell >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] PowerShell 可用（推荐使用）
) else (
    echo [INFO] PowerShell 不可用
)

where code >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] VS Code 已安装
) else (
    echo [INFO] VS Code 未检测到（可选）
)
echo.

REM 总结
echo.
echo ==========================================
echo   验证结果
echo ==========================================
echo.
echo 平台: Windows (原生)
echo 错误: %ERRORS%
echo 警告: %WARNINGS%
echo.

if %ERRORS% equ 0 (
    echo [SUCCESS] 环境验证通过！
    echo.
    echo 下一步操作:
    if defined MAVEN_CMD (
        echo   1. 编译: !MAVEN_CMD! clean compile
        echo   2. 测试: !MAVEN_CMD! test
        echo   3. 打包: !MAVEN_CMD! package
        echo   4. 运行: !MAVEN_CMD! exec:java -Dexec.mainClass="com.team20.editor.Main"
    )
    echo.
    echo 提示: 推荐使用 PowerShell 以获得更好体验
    exit /b 0
) else (
    echo [ERROR] 发现 %ERRORS% 个错误
    echo.
    echo 修复建议:
    if !ERRORS! gtr 0 echo   - 检查上述 [ERROR] 项
    if !WARNINGS! gtr 0 echo   - 建议修复 [WARN] 项
    exit /b 1
)
EOF
