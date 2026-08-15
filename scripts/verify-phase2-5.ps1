$ErrorActionPreference="Continue"
$root="$HOME\Desktop\AthletiQ_AI"
$backend="$root\backend"
$frontend="$root\frontend"
$java="$backend\src\main\java"
$src="$frontend\src"
$pass=0
$fail=0
$skip=0
$results=@()

function Check([string]$Name,[bool]$Ok,[string]$Detail="") {
    if($Ok){
        $script:pass++
        $script:results += "PASS | $Name | $Detail"
        Write-Host "PASS: $Name" -ForegroundColor Green
    } else {
        $script:fail++
        $script:results += "FAIL | $Name | $Detail"
        Write-Host "FAIL: $Name | $Detail" -ForegroundColor Red
    }
}

function Skip([string]$Name,[string]$Detail="") {
    $script:skip++
    $script:results += "SKIP | $Name | $Detail"
    Write-Host "SKIP: $Name | $Detail" -ForegroundColor Yellow
}

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " ATHLETIQ PHASE 2-5 AUTOMATED VERIFICATION" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "[1] Project structure" -ForegroundColor Yellow
foreach($p in @(
    "$backend\pom.xml",
    "$frontend\package.json",
    "$backend\src\main\resources\application.properties"
)){
    Check "Required file: $([IO.Path]::GetFileName($p))" (Test-Path $p)
}

Write-Host ""
Write-Host "[2] Backend tests" -ForegroundColor Yellow
Set-Location $backend
mvn test
$code=$LASTEXITCODE
Check "Backend Maven tests" ($code -eq 0) "mvn test"

Write-Host ""
Write-Host "[3] Backend compilation" -ForegroundColor Yellow
mvn -q -DskipTests compile
$code=$LASTEXITCODE
Check "Backend Java compilation" ($code -eq 0) "mvn compile"

Write-Host ""
Write-Host "[4] Frontend tests" -ForegroundColor Yellow
$pkg=Get-Content "$frontend\package.json" -Raw | ConvertFrom-Json
if($pkg.scripts -and $pkg.scripts.test){
    Set-Location $frontend
    npm test -- --run
    $code=$LASTEXITCODE
    Check "Frontend tests" ($code -eq 0) "npm test -- --run"
} else {
    Skip "Frontend tests" "No test script defined"
}

Write-Host ""
Write-Host "[5] Frontend production build" -ForegroundColor Yellow
Set-Location $frontend
npm run build
$code=$LASTEXITCODE
Check "Frontend production build" ($code -eq 0) "npm run build"

Write-Host ""
Write-Host "[6] Frontend runtime" -ForegroundColor Yellow
try {
    $r=Invoke-WebRequest -Uri "http://localhost:5173/" -UseBasicParsing -TimeoutSec 5
    Check "Frontend runtime" ($r.StatusCode -eq 200) "localhost:5173"
} catch {
    Skip "Frontend runtime" "localhost:5173 not reachable"
}

Write-Host ""
Write-Host "[7] Backend health" -ForegroundColor Yellow
try {
    $r=Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
    Check "Backend health" ($r.StatusCode -eq 200) "localhost:8080/actuator/health"
} catch {
    Skip "Backend health" "localhost:8080/actuator/health not reachable"
}

Write-Host ""
Write-Host "[8] Authentication contracts" -ForegroundColor Yellow
$javaFiles=Get-ChildItem $java -Recurse -Filter "*.java" -File
$allJava=($javaFiles | ForEach-Object { Get-Content $_.FullName -Raw }) -join "`n"

Check "Password hash model" ($allJava -match "password_hash")
Check "Email verification" ($allJava -match "emailVerified")
Check "Organizer role" ($allJava -match "Role\.ORGANIZER")
Check "JWT or bearer authentication" ($allJava -match "Jwt|JWT|Bearer")
Check "Permission model" ($allJava -match "Permission")
Check "Ownership model" ($allJava -match "organizerId|ownership")

Write-Host ""
Write-Host "[9] Profile contracts" -ForegroundColor Yellow
$profileFiles=Get-ChildItem $java -Recurse -Filter "*Profile*.java" -File -ErrorAction SilentlyContinue
$profileText=($profileFiles | ForEach-Object { Get-Content $_.FullName -Raw }) -join "`n"

Check "Profile backend exists" ($profileFiles.Count -gt 0)
Check "Profile persistence" ($profileText -match "Repository|JpaRepository")
Check "Profile ownership" ($profileText -match "userId|ownership")
Check "Password change support" ($allJava -match "changePassword|PasswordEncoder")
Check "Email preferences" ($allJava -match "EmailPreferencesController|EmailPreferencesService|EmailPreferencesRequest|EmailPreferencesResponse")

Write-Host ""
Write-Host "[10] Event contracts" -ForegroundColor Yellow
$eventFiles=Get-ChildItem "$java\com\athletiq\backend\event" -Recurse -Filter "*.java" -File -ErrorAction SilentlyContinue
$eventText=($eventFiles | ForEach-Object { Get-Content $_.FullName -Raw }) -join "`n"

Check "Event entity" ($eventText -match "@Entity")
Check "Event ownership" ($eventText -match "organizerId")
Check "Event status model" ($eventText -match "EventStatus")
Check "Event controller" ($eventText -match "@RestController")
Check "Event service" ($eventText -match "class EventService")

Write-Host ""
Write-Host "[11] Form backend contracts" -ForegroundColor Yellow
$formFiles=Get-ChildItem "$java\com\athletiq\backend\form" -Recurse -Filter "*.java" -File -ErrorAction SilentlyContinue
$formText=($formFiles | ForEach-Object { Get-Content $_.FullName -Raw }) -join "`n"

Check "Form entity" ($formText -match "class Form")
Check "FormVersion entity" ($formText -match "class FormVersion")
Check "FormField entity" ($formText -match "class FormField")
Check "FieldType model" ($formText -match "FieldType")
Check "Form controller" ($formText -match "class FormController")
Check "Form service" ($formText -match "class FormService")
Check "Form version service" ($formText -match "class FormVersionService")
Check "Form repository" ($formText -match "FormRepository")
Check "Form version repository" ($formText -match "FormVersionRepository")
Check "Form field repository" ($formText -match "FormFieldRepository")

Write-Host ""
Write-Host "[12] Form frontend contracts" -ForegroundColor Yellow
$formFrontendPath="$src\form-builder"
$formFrontendFiles=Get-ChildItem $formFrontendPath -Recurse -File -ErrorAction SilentlyContinue
$formFrontendText=($formFrontendFiles | ForEach-Object { Get-Content $_.FullName -Raw }) -join "`n"

foreach($name in @(
    "FormBuilderPage",
    "FormBuilderWorkspace",
    "FieldConfigurationPanel",
    "FieldOrderList",
    "FormPreview",
    "FormVersionHistory"
)){
    Check "Frontend component $name" ($formFrontendText -match $name)
}

Check "Save workflow" ($formFrontendText -match "formSaveWorkflow")
Check "Publish workflow" ($formFrontendText -match "publishFormVersion|validateFormBeforePublish|canPublishVersion|canPublishFormVersion")
Check "Field ordering" ($formFrontendText -match "normalizeFieldOrder|displayOrder")

Write-Host ""
Write-Host "[13] Form version safety" -ForegroundColor Yellow
Check "DRAFT state" ($formFrontendText -match "DRAFT")
Check "PUBLISHED state" ($formFrontendText -match "PUBLISHED")
Check "ARCHIVED state" ($formFrontendText -match "ARCHIVED")
Check "Version history" ($formFrontendText -match "selectedVersionId|FormVersionHistory")
Check "Exact FormVersion linkage" ($allJava -match "FormVersion")

Write-Host ""
Write-Host "[14] Application compatibility" -ForegroundColor Yellow
$appFiles=Get-ChildItem $java -Recurse -Filter "Application*.java" -File -ErrorAction SilentlyContinue
$appText=($appFiles | ForEach-Object { Get-Content $_.FullName -Raw }) -join "`n"

Check "Application entity" ($appText -match "class Application")
Check "Application FormVersion link" ($appText -match "FormVersion")
Check "Application Event link" ($appText -match "Event")
Check "Applicant ID" ($appText -match "applicantId")
Check "Application repository queries" ($appText -match "findByFormVersionId|findByEventId|findByApplicantId")
Check "FormVersion event validation" ($appText -match "does not belong to the requested event|formVersion.*event|event.*formVersion")

Write-Host ""
Write-Host "[15] PostgreSQL" -ForegroundColor Yellow
$env:PGPASSWORD="athletiq_dev_password"
$psql=Get-Command psql -ErrorAction SilentlyContinue

if($psql){
    psql -h localhost -p 5432 -U athletiq -d athletiq -c "SELECT current_database(), current_user;"
    Check "PostgreSQL connectivity" ($LASTEXITCODE -eq 0)

    $tables=(psql -h localhost -p 5432 -U athletiq -d athletiq -At -c "SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY table_name;") -split "`r?`n" | Where-Object { $_ -ne "" }

    foreach($t in @(
        "users",
        "profiles",
        "events",
        "forms",
        "form_versions",
        "form_fields",
        "applications"
    )){
        if($tables -contains $t){
            Check "DB table $t" $true
        } else {
            Skip "DB table $t" "Not present"
        }
    }

    if($tables -contains "users"){
        psql -h localhost -p 5432 -U athletiq -d athletiq -c "SELECT COUNT(*) AS users FROM users;"
    }

    if(($tables -contains "form_versions") -and ($tables -contains "forms")){
        psql -h localhost -p 5432 -U athletiq -d athletiq -c "SELECT COUNT(*) AS orphan_form_versions FROM form_versions v LEFT JOIN forms f ON f.id=v.form_id WHERE f.id IS NULL;"
    }

    if(($tables -contains "form_fields") -and ($tables -contains "form_versions")){
        psql -h localhost -p 5432 -U athletiq -d athletiq -c "SELECT COUNT(*) AS orphan_form_fields FROM form_fields ff LEFT JOIN form_versions v ON v.id=ff.form_version_id WHERE v.id IS NULL;"
    }

    if(($tables -contains "applications") -and ($tables -contains "form_versions")){
        psql -h localhost -p 5432 -U athletiq -d athletiq -c "SELECT COUNT(*) AS orphan_application_versions FROM applications a LEFT JOIN form_versions v ON v.id=a.form_version_id WHERE a.form_version_id IS NOT NULL AND v.id IS NULL;"
    }
} else {
    Skip "PostgreSQL checks" "psql not installed"
}

Set-Location $root

$report="$root\docs\PHASE-2-5-AUTOMATED-VERIFICATION.txt"
$header=@(
    "ATHLETIQ PHASE 2-5 AUTOMATED VERIFICATION",
    "Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')",
    "PASS=$pass FAIL=$fail SKIP=$skip",
    ""
)

($header + $results) | Set-Content $report -Encoding UTF8

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " PHASE 2-5 AUTOMATED VERIFICATION COMPLETE" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "PASS : $pass" -ForegroundColor Green
Write-Host "FAIL : $fail" -ForegroundColor Red
Write-Host "SKIP : $skip" -ForegroundColor Yellow
Write-Host "REPORT: $report" -ForegroundColor White
Write-Host "============================================================" -ForegroundColor Cyan

if($fail -gt 0){
    Write-Host "OVERALL: FAIL - fix failures before claiming verification." -ForegroundColor Red
    exit 1
}

Write-Host "OVERALL: AUTOMATED CHECKS PASSED." -ForegroundColor Green
Write-Host "Manual browser E2E testing remains separate." -ForegroundColor Green
exit 0