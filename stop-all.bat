@echo off
setlocal

set BASEDIR=%cd%

echo Killing processes on used ports...

call :killPort 9000
call :killPort 8888
call :killPort 8081
call :killPort 8082
call :killPort 8083
call :killPort 8084
call :killPort 9901
call :killPort 9902
call :killPort 9903

echo Stopping all Docker Compose services...

call :stopDockerCompose "api-gateway"
call :stopDockerCompose "auth-service"
call :stopDockerCompose "customer-service"
call :stopDockerCompose "marketdata-service"
call :stopDockerCompose "marketrealtime-service"
call :stopDockerCompose "notification-service"
call :stopDockerCompose "topup-service"
call :stopDockerCompose "trade-service"
call :stopDockerCompose "tradeprocessor-service"


echo Done.
exit /b

:stopDockerCompose
set "SERVICE_DIR=%~1"
set "FULL_DIR=%BASEDIR%\%SERVICE_DIR%"
set "DOCKER_COMPOSE_FILE=%FULL_DIR%\docker-compose.yml"

if exist "%DOCKER_COMPOSE_FILE%" (
    echo Shutting down docker-compose for %SERVICE_DIR%...
    pushd "%FULL_DIR%"
    docker-compose down
    popd
)
exit /b

:killPort
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%1 ^| findstr LISTENING') do (
    echo Killing PID %%a on port %1
    taskkill /PID %%a /F >nul 2>&1
)
exit /b
