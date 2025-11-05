@echo off
REM Team20 统一构建脚本 - Windows

setlocal

if exist "mvnw.cmd" (
    set MAVEN=mvnw.cmd
) else (
    set MAVEN=mvn
)

if "%1"=="" goto usage
if "%1"=="compile" goto compile
if "%1"=="build" goto compile
if "%1"=="test" goto test
if "%1"=="package" goto package
if "%1"=="run" goto run
if "%1"=="clean" goto clean
if "%1"=="verify" goto verify
goto usage

:compile
echo 编译项目...
%MAVEN% clean compile
goto end

:test
echo 运行测试...
%MAVEN% test
goto end

:package
echo 打包项目...
%MAVEN% clean package
goto end

:run
echo 运行程序...
%MAVEN% exec:java -Dexec.mainClass="com.team20.editor.Main"
goto end

:clean
%MAVEN% clean
goto end

:verify
echo 完整验证...
%MAVEN% clean verify
goto end

:usage
echo 用法: %0 {compile^|test^|package^|run^|clean^|verify}
echo.
echo   compile  - 编译项目
echo   test     - 运行测试
echo   package  - 打包为 JAR
echo   run      - 运行主程序
echo   clean    - 清理构建文件
echo   verify   - 完整验证
exit /b 1

:end
exit /b 0
EOF
