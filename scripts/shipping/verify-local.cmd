@echo off
setlocal
pushd "%~dp0..\.."
wsl.exe -d Ubuntu-22.04 -- bash scripts/shipping/run-local-tests.sh %*
set taskExit=%errorlevel%
popd
exit /b %taskExit%
