$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$environmentFile = Join-Path $projectRoot '.env.local'
$backendDirectory = Join-Path $projectRoot 'backend'
$jdkDirectory = Join-Path $projectRoot '.tools\jdk21-portable\jdk-21.0.12.1+1'

if (-not (Test-Path -LiteralPath $environmentFile)) {
    throw 'Missing .env.local. Copy .env.example and add local secret values.'
}

foreach ($line in Get-Content -LiteralPath $environmentFile) {
    if ($line -match '^\s*#' -or [string]::IsNullOrWhiteSpace($line)) { continue }
    $parts = $line -split '=', 2
    if ($parts.Count -ne 2) { continue }
    [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1], 'Process')
}

foreach ($required in @('DB_URL','DB_USER','DB_PASSWORD','JWT_SECRET','GEMINI_API_KEY')) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($required, 'Process'))) {
        throw "Missing required local environment variable: $required"
    }
}

$env:JAVA_HOME = $jdkDirectory
$env:Path = "$jdkDirectory\bin;$env:Path"
Set-Location -LiteralPath $backendDirectory
& .\mvnw.cmd spring-boot:run -q
