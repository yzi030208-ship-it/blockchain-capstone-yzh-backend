@echo off
setlocal
set "taskPython=%USERPROFILE%\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe"
if exist "%taskPython%" (
    "%taskPython%" "%~dp0capture-state.py" --order 0 --order 1 --final
) else (
    python "%~dp0capture-state.py" --order 0 --order 1 --final
)
exit /b %errorlevel%
