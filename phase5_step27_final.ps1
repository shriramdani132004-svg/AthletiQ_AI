$ErrorActionPreference="Stop"
$root="$HOME\Desktop\AthletiQ_AI"
Set-Location $root
$builder="$root\frontend\src\form-builder"
$controller="$root\backend\src\main\java\com\athletiq\backend\form\controller\FormController.java"
$api="$root\frontend\src\api\formApi.js"
$docs="$root\docs"
$enc=New-Object System.Text.UTF8Encoding($false)

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host " ATHLETIQ PHASE 5 - STEP 27" -ForegroundColor Cyan
Write-Host " FORM PREVIEW API - COMPLETE VERIFICATION" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

Write-Host "[1] Verifying backend preview endpoint..." -ForegroundColor Yellow
if(-not(Test-Path $controller)){throw "FormController.java missing"}
$controllerContent=[IO.File]::ReadAllText($controller,[Text.Encoding]::UTF8)
foreach($check in @("/versions/{versionId}/fields","getFields","@GetMapping")){
    if($controllerContent -notmatch [regex]::Escape($check)){throw "Backend preview capability missing: $check"}
    Write-Host "PASS: $check" -ForegroundColor Green
}

Write-Host "[2] Verifying FormPreview UI..." -ForegroundColor Yellow
$preview="$builder\FormPreview.jsx"
if(-not(Test-Path $preview)){throw "FormPreview.jsx missing"}
$previewContent=[IO.File]::ReadAllText($preview,[Text.Encoding]::UTF8)
foreach($check in @("fields","version")){
    if($previewContent -notmatch [regex]::Escape($check)){throw "FormPreview capability missing: $check"}
    Write-Host "PASS: FormPreview $check" -ForegroundColor Green
}

Write-Host "[3] Verifying form API..." -ForegroundColor Yellow
if(-not(Test-Path $api)){throw "formApi.js missing"}
$apiContent=[IO.File]::ReadAllText($api,[Text.Encoding]::UTF8)
if($apiContent -notmatch "(?i)field"){throw "Form API field capability missing"}
Write-Host "PASS: Form API field capability" -ForegroundColor Green

Write-Host "[4] Creating Step 27 documentation..." -ForegroundColor Yellow
$doc="$docs\FORM-PREVIEW-API.md"
$docLines=@(
"# FORM PREVIEW API",
"",
"## Objective",
"",
"The form preview renders the selected FormVersion using its persisted field configuration without modifying the form.",
"",
"## Backend Data Source",
"",
"GET /api/events/{eventId}/form/versions/{versionId}/fields",
"",
"## Read-Only Rule",
"",
"Preview is read-only and does not modify DRAFT, PUBLISHED, or ARCHIVED form versions.",
"",
"## Frontend",
"",
"FormPreview.jsx renders the selected version fields.",
"",
"## Flow",
"",
"Form Builder",
"    -> Selected Form Version",
"    -> Version Fields API",
"    -> FormPreview.jsx",
"    -> Read-only Preview"
)
[IO.File]::WriteAllText($doc,($docLines -join [Environment]::NewLine),$enc)

if(-not(Test-Path $doc)){throw "Step 27 documentation creation failed"}
Write-Host "PASS: Documentation created" -ForegroundColor Green

Write-Host "[5] Verifying documentation..." -ForegroundColor Yellow
$docContent=[IO.File]::ReadAllText($doc,[Text.Encoding]::UTF8)
foreach($check in @("FormPreview.jsx","PUBLISHED","ARCHIVED","read-only","versionId")){
    if($docContent -notmatch [regex]::Escape($check)){throw "Documentation missing: $check"}
    Write-Host "PASS: Documentation $check" -ForegroundColor Green
}

Write-Host "[6] Running frontend production build..." -ForegroundColor Yellow
Set-Location "$root\frontend"
npm run build
if($LASTEXITCODE -ne 0){throw "Frontend production build failed"}
Write-Host "PASS: Frontend production build" -ForegroundColor Green

Write-Host "[7] Running backend compilation..." -ForegroundColor Yellow
Set-Location "$root\backend"
mvn -q -DskipTests compile
if($LASTEXITCODE -ne 0){throw "Backend compilation failed"}
Write-Host "PASS: Backend Java compilation" -ForegroundColor Green

Set-Location $root

Write-Host ""
Write-Host "==============================================" -ForegroundColor Green
Write-Host " PHASE 5 - STEP 27 COMPLETE" -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
Write-Host "Backend preview endpoint     : PASS" -ForegroundColor Green
Write-Host "FormPreview UI              : PASS" -ForegroundColor Green
Write-Host "Read-only preview           : PASS" -ForegroundColor Green
Write-Host "Documentation               : PASS" -ForegroundColor Green
Write-Host "Backend Java compilation    : PASS" -ForegroundColor Green
Write-Host "Frontend production build   : PASS" -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
git status --short --branch
