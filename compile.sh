#!/bin/bash

# Compile Script for Document Editor
# Compiles all Java source files

echo "=========================================="
echo "  Document Editor - Compilation Script"
echo "=========================================="
echo ""

# Navigate to source directory
cd "$(dirname "$0")/src/main/java"

echo "Compiling Java source files..."
echo ""

# Create output directory if it doesn't exist
mkdir -p ../../../out

# Compile with detailed output (including Main.java)
javac -d ../../../out -verbose com/documenteditor/Main.java com/documenteditor/**/*.java 2>&1 | head -20

# Check compilation status
if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Compilation successful!"
    echo ""
    echo "To run the application:"
    echo "  cd out"
    echo "  java com.documenteditor.Main"
    echo ""
else
    echo ""
    echo "✗ Compilation failed!"
    echo "Please check the error messages above."
    echo ""
    exit 1
fi
