$ErrorActionPreference = 'Stop'
$taskRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$taskSecret = Read-Host 'Your own SHIPPING arbiter test-wallet private key (hidden; never paste in chat)' -AsSecureString
$taskPointer = [IntPtr]::Zero
$taskHadKey = Test-Path Env:WEB3J_PRIVATE_KEY
$taskPreviousKey = $env:WEB3J_PRIVATE_KEY
try {
    $taskPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($taskSecret)
    $env:WEB3J_PRIVATE_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($taskPointer)
    if ($env:WEB3J_PRIVATE_KEY -notmatch '^(0x)?[0-9a-fA-F]{64}$') {
        throw 'Expected a 32-byte test-wallet private key. Value suppressed.'
    }
    Push-Location $taskRoot
    try {
        Write-Host 'Starting on 127.0.0.1:8081. In a second terminal, check GET /api/shipping/escrow/account before any deploy.'
        & .\gradlew.bat bootRun '--args=--server.port=8081' --console=plain
        if ($LASTEXITCODE -ne 0) { throw 'Backend stopped with an error; private key value suppressed.' }
    } finally {
        Pop-Location
    }
} finally {
    if ($taskPointer -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($taskPointer) }
    $taskSecret.Dispose()
    if ($taskHadKey) { $env:WEB3J_PRIVATE_KEY = $taskPreviousKey }
    else { Remove-Item Env:WEB3J_PRIVATE_KEY -ErrorAction SilentlyContinue }
    $taskPreviousKey = $null
}
