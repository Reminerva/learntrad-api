@echo off
setlocal
setlocal ENABLEDELAYEDEXPANSION

set BASEDIR=%cd%

:: Kill ports if already used
call :killPort 9000
call :killPort 8282
call :killPort 6385
call :killPort 3100
call :killPort 9191
call :killPort 3110
call :killPort 9411
call :killPort 3000
call :killPort 8081
call :killPort 5433
call :killPort 8082
call :killPort 5434
call :killPort 8083
call :killPort 5435
call :killPort 2181
call :killPort 9092
call :killPort 29092
call :killPort 9009
call :killPort 8084
call :killPort 5438
call :killPort 6381
call :killPort 9902
call :killPort 9901
call :killPort 9903
call :killPort 5436
call :killPort 6380

:: Docker compose
call :dockerUp "marketdata-service"
call :dockerUp "trade-service"
call :dockerUp "api-gateway"
call :dockerUp "auth-service"
call :dockerUp "customer-service"
call :dockerUp "notification-service"
call :dockerUp "topup-service"
call :dockerUp "tradeprocessor-service"
call :dockerUp "marketrealtime-service"

:: Docker compose
call :serviceStartSpecific "trade_postgres"
call :serviceStartSpecific "zookeeper"
call :serviceStartSpecific "broker"
call :serviceStartSpecific "customer_postgres"
call :serviceStartSpecific "topup_postgres"
call :serviceStartSpecific "tradeprocessor_postgres"
call :serviceStartSpecific "marketdata_timescale"


goto :eof

:dockerUp
set "SERVICE_DIR=%~1"
set "FULL_DIR=%BASEDIR%\%SERVICE_DIR%"
set "DOCKER_COMPOSE_FILE=%FULL_DIR%\docker-compose.yml"
set "CONTAINER_NAME=%SERVICE_DIR%_container"

if exist "%DOCKER_COMPOSE_FILE%" (
    echo Running docker-compose in %SERVICE_DIR%...
    pushd "%FULL_DIR%"
    docker-compose up -d
    
    popd
)

exit /b

:killPort
echo Killing process on port %1 (if any)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%1 ^| findstr LISTENING') do (
    echo Found PID %%a using port %1, killing...
    taskkill /PID %%a /F >nul 2>&1
)
exit /b

:serviceStartSpecific
set "CONTAINER_TO_CHECK=%~1"
set "SERVICE_DIR_FOR_MAVEN=%~2"
set "FULL_DIR_FOR_MAVEN=%BASEDIR%\%SERVICE_DIR_FOR_MAVEN%"

echo Waiting for container %CONTAINER_TO_CHECK% to become healthy...

set /a counter=0
:wait_loop_specific
set "STATUS=unknown"
for /f "tokens=*" %%i in ('docker inspect -f "{{.State.Health.Status}}" "%CONTAINER_TO_CHECK%" 2^>nul') do (
    set "STATUS=%%i"
)
if "!STATUS!" NEQ "healthy" (
    set /a counter+=1
    if !counter! GEQ 300 (
        echo [ERROR] Timeout waiting for %CONTAINER_TO_CHECK% to be healthy.
        exit /b 1
    )
    if "%CONTAINER_TO_CHECK%"=="marketdata_timescale" (
        echo [!counter! sec] %CONTAINER_TO_CHECK% is !STATUS!... Copying historical data to the database may take some time.
        timeout /t 1 >nul
    )
    if not "%CONTAINER_TO_CHECK%"=="marketdata_timescale" (
        echo [!counter! sec] %CONTAINER_TO_CHECK% is !STATUS!... waiting...
        timeout /t 1 >nul
    )
    goto :wait_loop_specific
)
echo %CONTAINER_TO_CHECK% is healthy.

if not "%SERVICE_DIR_FOR_MAVEN%"=="" (
    echo Starting %SERVICE_DIR_FOR_MAVEN%...
    wt new-tab -d "%FULL_DIR_FOR_MAVEN%" cmd /k ".\mvnw spring-boot:run"
)

exit /b

:serviceStart
set "SERVICE_DIR_FOR_MAVEN=%~1"
set "FULL_DIR_FOR_MAVEN=%BASEDIR%\%SERVICE_DIR_FOR_MAVEN%"

if not "%SERVICE_DIR_FOR_MAVEN%"=="" (
    echo Starting %SERVICE_DIR_FOR_MAVEN%...
    wt new-tab -d "%FULL_DIR_FOR_MAVEN%" cmd /k ".\mvnw spring-boot:run"
)