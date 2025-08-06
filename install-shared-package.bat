@echo off
setlocal

set MODULES=shared-client-external shared-constant shared-jwt-util shared-model-util

echo ===============================
echo   Building shared modules...
echo ===============================

for %%D in (%MODULES%) do (
    echo.
    echo ========== Building %%D ==========
    call mvn -f "%%D/pom.xml" clean install
    if errorlevel 1 (
        echo Build failed in %%D
        exit /b 1
    )
)

echo.
echo All modules built successfully!
endlocal
