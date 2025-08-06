@echo off
setlocal

set BASEDIR=%cd%

call :killPort 9000
call :killPort 8888
call :killPort 8081
call :killPort 8082
call :killPort 8083
call :killPort 8084
call :killPort 9901
call :killPort 9902
call :killPort 9903

echo ============================================
echo   Starting Spring Boot Applications...
echo ============================================

wt ^
new-tab -d "%BASEDIR%\api-gateway" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\auth-service" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\customer-service" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\marketdata-service" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\marketrealtime-service" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\notification-service" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\topup-service" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\trade-service" cmd /k ".\mvnw spring-boot:run" ^
; new-tab -d "%BASEDIR%\tradeprocessor-service" cmd /k ".\mvnw spring-boot:run"

goto :eof

:killPort
echo Killing process on port %1 (if any)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%1 ^| findstr LISTENING') do (
    echo Found PID %%a using port %1, killing...
    taskkill /PID %%a /F >nul 2>&1
)
exit /b