$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$sourceRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$deliveryDirectory = Join-Path $sourceRoot 'entrega'
New-Item -ItemType Directory -Path $deliveryDirectory -Force | Out-Null
$archivePath = Join-Path $deliveryDirectory 'COBRA360_PC2_Codigo.zip'
$stream = [System.IO.File]::Open($archivePath,[System.IO.FileMode]::Create)
$archive = [System.IO.Compression.ZipArchive]::new($stream,[System.IO.Compression.ZipArchiveMode]::Create)
$count = 0
try {
    foreach ($sourceFile in Get-ChildItem -LiteralPath $sourceRoot -File -Recurse -Force) {
        $relative = [System.IO.Path]::GetRelativePath($sourceRoot,$sourceFile.FullName).Replace('\','/')
        if ($relative -match '^(\.git/|\.firecrawl/|\.runtime/|entrega/)' -or $relative -match '(^|/)(target|node_modules)/' -or $relative -eq '.env') { continue }
        [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($archive,$sourceFile.FullName,('COBRA360/' + $relative),[System.IO.Compression.CompressionLevel]::Optimal) | Out-Null
        $count++
    }
} finally { $archive.Dispose(); $stream.Dispose() }
Write-Output "Código actualizado: $archivePath ($count archivos; sin outputs ni credenciales locales)."
