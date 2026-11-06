# Download Inter font files for the project
# Run this script from the project root: .\download_fonts.ps1

$FontDir = "app\src\main\res\font"
New-Item -ItemType Directory -Force -Path $FontDir | Out-Null

Write-Host "Downloading Inter font family..."

$baseUrl = "https://github.com/rsms/inter/releases/download/v4.0/"
$weights = @("Regular", "Medium", "SemiBold", "Bold", "Black")

foreach ($weight in $weights) {
    $filename = "Inter-${weight}.ttf"
    $url = $baseUrl + $filename
    $outputPath = $FontDir + "\inter_" + $weight.ToLower() + ".ttf"
    
    Write-Host "Downloading $filename ..."
    try {
        Invoke-WebRequest -Uri $url -OutFile $outputPath -UseBasicParsing
        Write-Host "Saved to $outputPath"
    } catch {
        Write-Warning "Failed to download $filename"
    }
}

Write-Host "Font files:"
Get-ChildItem $FontDir

Write-Host ""
Write-Host "Note: If downloads fail, manually download from:"
Write-Host "https://github.com/rsms/inter/releases/tag/v4.0"
Write-Host "or https://fonts.google.com/specimen/Inter"