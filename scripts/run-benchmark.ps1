Write-Host ""
Write-Host "=========================================="
Write-Host "WEXA AI - Graph Database Benchmark"
Write-Host "=========================================="
Write-Host ""

if ([string]::IsNullOrWhiteSpace($env:COGNODB_URI)) {
    Write-Error "COGNODB_URI is not set."
    exit 1
}

if ([string]::IsNullOrWhiteSpace($env:COGNODB_USERNAME)) {
    Write-Error "COGNODB_USERNAME is not set."
    exit 1
}

if ([string]::IsNullOrWhiteSpace($env:COGNODB_PASSWORD)) {
    Write-Error "COGNODB_PASSWORD is not set."
    exit 1
}

Write-Host "CognoDB environment variables detected."
Write-Host "Building benchmark..."

.\mvnw.cmd clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed."
    exit 1
}

Write-Host ""
Write-Host "Build successful."
Write-Host ""
Write-Host "Starting benchmark..."
Write-Host "Existing CognoDB dataset will be reused."
Write-Host ""

java -jar target\cognodb-benchmark-1.0.0.jar

if ($LASTEXITCODE -ne 0) {
    Write-Error "Benchmark failed."
    exit 1
}

Write-Host ""
Write-Host "=========================================="
Write-Host "Benchmark finished"
Write-Host "Result: results\cognodb.json"
Write-Host "=========================================="
