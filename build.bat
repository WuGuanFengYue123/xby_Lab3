@echo off
REM Team20 Windows build/run script (equivalent to the provided POSIX build.sh)
REM Usage: build.bat run

SETLOCAL ENABLEDELAYEDEXPANSION
REM change to script directory
cd /d "%~dp0"

REM detect mvnw wrapper (Windows) or use mvn on PATH
if exist "%~dp0mvnw.cmd" (
  set "MAVEN=%~dp0mvnw.cmd"
) else if exist "%~dp0mvnw" (
  REM rare case: plain mvnw executable; call through sh if present. Prefer mvn in PATH on Windows.
  set "MAVEN=mvn"
) else (
  set "MAVEN=mvn"
)

set "MAIN_MODULE_DIR=core"
set "PLUGIN_DIR=plugins"

if "%~1"=="" goto :usage

if /I "%~1"=="run" (

  REM discover plugins
  set "PLUGINS="
  if exist "%PLUGIN_DIR%" (
    for /d %%D in ("%PLUGIN_DIR%\*") do (
      if exist "%%D\pom.xml" (
        REM store full path relative to repo root (no trailing backslash)
        set "p=%%~fD"
        REM convert to relative path if possible (keep original behavior in POSIX script)
        REM Append to PLUGINS with a pipe separator to iterate later
        if defined PLUGINS (
          set "PLUGINS=!PLUGINS!|%%~fD"
        ) else (
          set "PLUGINS=%%~fD"
        )
      )
    )
  )

  REM build modules (core + discovered plugins)
  set "modules=%MAIN_MODULE_DIR%"
  if defined PLUGINS (
    for %%P in (!PLUGINS:^=^|!) do (
      REM for /f splits by default tokens; use delayed expansion to read variable content
      for /f "delims=" %%R in ("%%P") do (
        set "modules=!modules!,%%~nxR"
      )
    )
  )

  echo Building modules: %modules%
  REM use -am -pl behaviour is supported by maven, but on Windows we pass full module list
  "%MAVEN%" -am -pl "%MAIN_MODULE_DIR%" -DskipTests clean package

  echo Copying core runtime dependencies...
  "%MAVEN%" -f "%MAIN_MODULE_DIR%\pom.xml" -q dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="%CD%\%MAIN_MODULE_DIR%\target\dependency"

  echo Copying plugin runtime dependencies...
  if defined PLUGINS (
    for %%P in (!PLUGINS:^=^|!) do (
      for /f "delims=" %%R in ("%%P") do (
        echo Copying runtime dependencies for plugin: %%~nxR
        set "outdir=%CD%\%%~nxR\target\dependency"
        if not exist "!outdir!" mkdir "!outdir!"
        REM exclude core artifact(s) to avoid duplicate classes at runtime
        "%MAVEN%" -f "%%~fR\pom.xml" -q dependency:copy-dependencies -DincludeScope=runtime -DexcludeGroupIds=com.team20 -DexcludeArtifactIds=text-editor -DoutputDirectory="!outdir!"
      )
    )
  )

  REM assemble classpath
  set "CP="

  REM add core classes
  if exist "%CD%\%MAIN_MODULE_DIR%\target\classes" (
    set "CP=%CP%;%CD%\%MAIN_MODULE_DIR%\target\classes"
  )

  REM add jars from core dependency dir
  if exist "%CD%\%MAIN_MODULE_DIR%\target\dependency" (
    for %%J in ("%CD%\%MAIN_MODULE_DIR%\target\dependency\*.jar") do (
      if exist "%%~fJ" set "CP=!CP!;%%~fJ"
    )
  )

  REM add plugin classes and jars
  if defined PLUGINS (
    for %%P in (!PLUGINS:^=^|!) do (
      for /f "delims=" %%R in ("%%P") do (
        REM plugin target classes
        if exist "%%~fR\target\classes" (
          set "CP=!CP!;%%~fR\target\classes"
        )
        REM plugin target jars
        for %%PJ in ("%%~fR\target\*.jar") do (
          if exist "%%~fPJ" set "CP=!CP!;%%~fPJ"
        )
        REM plugin dependency jars
        if exist "%%~fR\target\dependency" (
          for %%PD in ("%%~fR\target\dependency\*.jar") do (
            if exist "%%~fPD" set "CP=!CP!;%%~fPD"
          )
        )
      )
    )
  )

  REM normalize CP (remove leading semicolon)
  if defined CP (
    if "!CP:~0,1!"==";" set "CP=!CP:~1!"
  )

  if not defined CP (
    echo ERROR: classpath empty
    exit /b 1
  )

  echo Running com.team20.editor.Main with classpath:
  REM print up to first 40 classpath entries (split by ;)
  setlocal enabledelayedexpansion
  set "count=0"
  for %%X in ("!CP:;=";"!") do (
    set /a count+=1
    echo   %%~X
    if !count! GEQ 40 goto :cp_done
  )
  :cp_done
  endlocal & set "count="

  REM launch Java
  java -cp "%CP%" com.team20.editor.Main

  echo.
  echo ✓ 操作完成！
  goto :eof
)

:usage
echo Usage: %~nx0 {run}
exit /b 1