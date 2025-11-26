@echo off
REM Compile Script for Document Editor (Windows)
REM Compiles all Java source files

echo ==========================================
echo   Document Editor - Compilation Script
echo ==========================================
echo.

REM Navigate to source directory
cd "%~dp0src\main\java"

echo Compiling Java source files...
echo.

REM Create output directory if it doesn't exist
if not exist "..\..\..\out" mkdir "..\..\..\out"

REM Compile all Java files
javac -d ..\..\..\out com\documenteditor\*.java ^
    com\documenteditor\adapter\*.java ^
    com\documenteditor\cli\*.java ^
    com\documenteditor\command\*.java ^
    com\documenteditor\exporter\*.java ^
    com\documenteditor\factory\*.java ^
    com\documenteditor\model\*.java ^
    com\documenteditor\observer\*.java ^
    com\documenteditor\strategy\*.java ^
    com\documenteditor\util\*.java ^
    com\documenteditor\visitor\*.java

REM Check compilation status
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Compilation successful!
    echo.
    echo To run the application:
    echo   cd out
    echo   java com.documenteditor.Main
    echo.
) else (
    echo.
    echo [ERROR] Compilation failed!
    echo Please check the error messages above.
    echo.
    exit /b 1
)

pause
