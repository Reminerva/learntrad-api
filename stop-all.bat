@echo off
setlocal

echo Stopping all services by killing ports...

call :killPort 9000
call :killPort 8888
call :killPort 8081
call :killPort 8082
call :killPort 8083
call :killPort 8084
call :killPort 9901
call :killPort 9903
call :killPort 9902

echo Done.
exit /b

:killPort
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%1 ^| findstr LISTENING') do (
    echo Killing PID %%a on port %1
    taskkill /PID %%a /F >nul 2>&1
)
exit /b
