param(
    [switch]$NoPause,
    [string]$TargetAbis = "arm64-v8a"
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $repoRoot

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
$java = Join-Path $env:JAVA_HOME "bin\java.exe"
$wrapperJar = Join-Path $repoRoot "gradle\wrapper\gradle-wrapper.jar"
$releaseApkDir = Join-Path $repoRoot "app\build\outputs\apk\release"
$outputDir = Join-Path $PSScriptRoot "output"
$outputApk = Join-Path $outputDir "NFCunlocker-release.apk"

function Exit-WithPause {
    param(
        [int]$ExitCode
    )

    if (-not $NoPause) {
        Read-Host "按 Enter 关闭窗口"
    }
    exit $ExitCode
}

try {
    Write-Host "正在构建 release APK..."
    Write-Host "目标架构: $TargetAbis"
    & $java "-Dorg.gradle.appname=gradlew" "-classpath" $wrapperJar "org.gradle.wrapper.GradleWrapperMain" ":app:assembleRelease" "-Pnfcunlocker.targetAbis=$TargetAbis" "--stacktrace"
    $buildExitCode = $LASTEXITCODE

    Write-Host ""
    if ($buildExitCode -ne 0) {
        Write-Host "Release 构建失败，退出码: $buildExitCode"
        Write-Host "窗口会保持打开，方便复制错误输出。"
        Exit-WithPause -ExitCode $buildExitCode
    }

    New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
    $sourceApks = @(Get-ChildItem -LiteralPath $releaseApkDir -Filter "*.apk" | Sort-Object LastWriteTime -Descending)

    if ($sourceApks.Count -eq 0) {
        throw "未找到 Release APK: $releaseApkDir"
    }

    if ($sourceApks.Count -eq 1) {
        Copy-Item -LiteralPath $sourceApks[0].FullName -Destination $outputApk -Force
    } else {
        foreach ($apk in $sourceApks) {
            $targetApk = Join-Path $outputDir ("NFCunlocker-release-" + $apk.BaseName + ".apk")
            Copy-Item -LiteralPath $apk.FullName -Destination $targetApk -Force
        }
    }

    Write-Host "Release 构建成功。"
    Get-ChildItem -LiteralPath $outputDir -Filter "NFCunlocker-release*.apk" | ForEach-Object {
        Write-Host "APK: $($_.FullName)"
    }
    Exit-WithPause -ExitCode 0
} catch {
    Write-Host ""
    Write-Host "Release 构建脚本执行失败:"
    Write-Host $_
    Write-Host "窗口会保持打开，方便复制错误输出。"
    Exit-WithPause -ExitCode 1
}
