@echo off
REM Team20 Windows build/run script
REM Usage: build.bat run

SETLOCAL ENABLEDELAYEDEXPANSION
cd /d "%~dp0"

REM -------------------------
REM detect maven wrapper
REM -------------------------
if exist "%~dp0mvnw.cmd" (
  set "MAVEN=%~dp0mvnw.cmd"
) else (
  set "MAVEN=mvn"
)

set "MAIN_MODULE_DIR=core"
set "PLUGIN_DIR=plugins"

if "%~1"=="" goto usage

if /I "%~1"=="run" (

  REM -------------------------
  REM discover plugins
  REM -------------------------
  set "PLUGIN_LIST_FILE=%TEMP%\xby_plugins.txt"
  if exist "%PLUGIN_LIST_FILE%" del /f /q "%PLUGIN_LIST_FILE%"

  if exist "%PLUGIN_DIR%" (
    for /f "delims=" %%D in ('dir /b /ad "%PLUGIN_DIR%" 2^>nul') do (
      if exist "%PLUGIN_DIR%\%%D\pom.xml" (
        echo %CD%\%PLUGIN_DIR%\%%D>>"%PLUGIN_LIST_FILE%"
      )
    )
  )

  REM -------------------------
  REM build modules: core + discovered plugins
  REM -------------------------
  set "modules=%MAIN_MODULE_DIR%"
  if exist "%PLUGIN_LIST_FILE%" (
    for /f "usebackq delims=" %%L in ("%PLUGIN_LIST_FILE%") do (
      set "modules=!modules!,%%~nxL"
    )
  )

  echo Building modules: %modules%
  "%MAVEN%" -am -pl "%modules%" -DskipTests clean install || (
    echo Maven build failed.
    exit /b 1
  )

  REM -------------------------
  REM copy core runtime dependencies
  REM -------------------------
  echo Copying core runtime dependencies...
  "%MAVEN%" -f "%MAIN_MODULE_DIR%\pom.xml" -q dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="%CD%\%MAIN_MODULE_DIR%\target\dependency"

  REM -------------------------
  REM copy plugin runtime dependencies
  REM -------------------------
  if exist "%PLUGIN_LIST_FILE%" (
    for /f "usebackq delims=" %%L in ("%PLUGIN_LIST_FILE%") do (
      echo Copying runtime dependencies for plugin: %%~nxL
      set "outdir=%CD%\%%~nxL\target\dependency"
      if not exist "!outdir!" mkdir "!outdir!"
      "%MAVEN%" -f "%%~fL\pom.xml" -q dependency:copy-dependencies -DincludeScope=runtime -DexcludeGroupIds=com.team20 -DexcludeArtifactIds=text-editor -DoutputDirectory="!outdir!"
    )
  )

  REM -------------------------
  REM assemble classpath
  REM -------------------------
  set "CP="

  REM add core classes
  if exist "%CD%\%MAIN_MODULE_DIR%\target\classes" (
    set "CP=%CD%\%MAIN_MODULE_DIR%\target\classes"
  )

  REM add jars from core dependency dir
  for %%J in ("%CD%\%MAIN_MODULE_DIR%\target\dependency\*.jar") do (
    if exist "%%~fJ" (
      set "CP=!CP!;%%~fJ"
    )
  )

  REM add plugin classes and jars
  if exist "%PLUGIN_LIST_FILE%" (
    for /f "usebackq delims=" %%L in ("%PLUGIN_LIST_FILE%") do (
      REM plugin target classes
      if exist "%%~fL\target\classes" (
        set "CP=!CP!;%%~fL\target\classes"
      )

      REM plugin target jars
      for %%J in ("%%~fL\target\*.jar") do (
        if exist "%%~fJ" set "CP=!CP!;%%~fJ"
      )

      REM plugin dependency jars
      if exist "%%~fL\target\dependency" (
        for %%D in ("%%~fL\target\dependency\*.jar") do (
          if exist "%%~fD" set "CP=!CP!;%%~fD"
        )
      )
    )
  )

  REM remove leading semicolon if present
  if defined CP (
    if "!CP:~0,1!"==";" set "CP=!CP:~1!"
  )

  if not defined CP (
    echo ERROR: classpath empty
    exit /b 1
  )

  REM -------------------------
  REM print first 40 elements of classpath
  REM -------------------------
  echo Running com.team20.editor.Main with classpath:
  set count=0
  for %%X in ("!CP:;=";"!") do (
    set /a count+=1
    echo   %%~X
    if !count! GEQ 40 goto cp_done
  )
  :cp_done

  REM -------------------------
  REM launch Java
  REM -------------------------
  java -cp "!CP!" com.team20.editor.Main

  echo.
  echo ✓ 操作完成！
  goto :eof
)

:usage
echo Usage: %~nx0 {run}
exit /b 1
