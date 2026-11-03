@echo off
setlocal
set "taskPython=%USERPROFILE%\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe"
if exist "%taskPython%" (
    "%taskPython%" "%~dp0check-restrictions.py"
) else (
    python "%~dp0check-restrictions.py"
)
exit /b %errorlevel%
