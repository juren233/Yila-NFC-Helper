<p align="center">
  <img src="app/src/main/res/drawable-nodpi/ic_launcher_generated.png" width="128" alt="易拉NFC辅助 Logo" />
</p>

<h1 align="center">易拉NFC辅助</h1>

<p align="center">
  <strong>面向 Android 用户，补上易拉开门没有适配自家 NFC 链接的那一环。</strong>
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-5.0%2B-3DDC84?logo=android&logoColor=white" />
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Android-7F52FF?logo=kotlin&logoColor=white" />
  <img alt="Version" src="https://img.shields.io/badge/version-1.2.0-blue" />
</p>

<p align="center">
  <a href="README.en.md">English</a>
</p>

## 它是干什么的

易拉NFC辅助（Yila NFC Helper）是一个面向 Android 用户的极简小工具，用来配合「易拉开门」使用。

易拉开门使用了 `yila://frank.lee.com` 这样的 NFC 链接，但 Android 版易拉开门 App 本身没有正确适配这个入口。结果是：标签里明明写着易拉自己的链接，Android 手机碰了 NFC 标签后却不能直接进入易拉开门并自动开门。

易拉NFC辅助补的就是这一环：它接收 `yila://frank.lee.com` NFC 标签唤起，再显式拉起易拉开门应用，让这个原本断掉的入口重新可用。

它不做复杂界面，不常驻后台，也不在最近任务里留下卡片。该出现时出现，完成转发后立即退出。

## 为什么需要它

很多人用手机开门时，真实流程并不顺：

- 到门口才发现还要找 App。
- App 图标藏在桌面文件夹里，打开慢。
- NFC 标签里写的是易拉自己的 `yila://frank.lee.com`，但 Android 版易拉开门 App 没有把这个入口适配好。
- 系统识别到了标签，后续却没有便捷地进入易拉开门并自动开门。

易拉NFC辅助解决的是这个断点：让 `yila://frank.lee.com` 这类 NFC 链接先进入辅助应用，再由辅助应用显式拉起易拉开门。

## 适合谁使用

- 经常使用易拉开门的 Android 用户。
- 希望把 NFC 标签贴在门边，用来快速开门的用户。
- 不想安装复杂自动化工具的用户。

## 使用方式

1. 安装易拉NFC辅助（本项目）。
2. 准备一个 NFC 标签。
3. 将 NFC 标签写入以下内容：
   | 项目           | 内容                         |
   | ------------ | -------------------------- |
   | URL / 链接     | `yila://frank.lee.com`     |
   | Android 应用包名 | `com.juren233.nfcunlocker` |
4. 用 Android 手机靠近 NFC 标签。
5. 系统识别标签后，易拉NFC辅助会拉起易拉开门。（首次拉起会触发链式启动，请确保选择始终允许以确保后续使用流畅性！）

## 兼容性说明

Android 16 增加了 NFC Tag Intent 偏好控制。易拉NFC辅助会在启动时尝试检查当前应用是否允许接收 NFC 标签事件。

- 如果系统明确返回“不允许”，应用会提示并跳转到系统 NFC 偏好设置页。
- 如果部分 ROM 查询状态时抛出 `SecurityException`，应用不会把它误判为权限关闭，而是继续保持可用。

这样做是因为有些 Android 16 手机虽然查询接口会报权限错误，但 NFC 标签实际仍能正常唤起应用。

## 它不会做什么

- 不破解门禁。
- 不绕过易拉开门本身的登录、授权或门禁权限。
- 不读取、保存或上传门禁凭据。
- 不常驻后台监听。
- 不替代易拉开门应用本身。

## 开发与构建

项目使用 Kotlin 和 Android Gradle Plugin 构建，推荐使用 JDK 21。

Release 构建命令：

```powershell
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts\build-release.ps1 -NoPause
```

构建成功后，APK 位于：

```text
scripts/output/NFCunlocker-release.apk
```

## 关键文件

| 文件                          | 说明                                |
| --------------------------- | --------------------------------- |
| `SetupActivity.kt`          | 启动入口，负责就绪提示和 Android 16 NFC 偏好检查。 |
| `RelayActivity.kt`          | NFC 转发入口，负责拉起易拉开门。                |
| `AndroidManifest.xml`       | 声明 NFC 标签过滤规则和无后台卡片行为。            |
| `scripts/build-release.ps1` | Release APK 构建脚本。                 |

## 开源许可证

本项目采用 Creative Commons Attribution-NonCommercial 4.0 International（CC BY-NC 4.0）授权。

你可以在保留署名的前提下共享和改编本项目内容，但不得将本项目用于商业目的。完整条款以 Creative Commons 官方协议为准，详见 [LICENSE.md](LICENSE.md)。
