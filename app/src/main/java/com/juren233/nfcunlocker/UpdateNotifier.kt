package com.juren233.nfcunlocker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateNotifier {
    private const val TAG = "UpdateNotifier"
    private const val CHANNEL_ID = "app_update"
    private const val NOTIFICATION_ID = 1002
    private const val LATEST_RELEASE_API =
        "https://api.github.com/repos/juren233/Yila-NFC-Helper/releases/latest"
    private const val RELEASES_LATEST_URL =
        "https://github.com/juren233/Yila-NFC-Helper/releases/latest"

    fun checkAndNotify(context: Context) {
        val appContext = context.applicationContext

        Thread {
            try {
                val latestRelease = fetchLatestRelease()
                val latestVersionCode = fetchVersionCode(latestRelease.tagName)

                if (latestVersionCode.toLong() > currentVersionCode(appContext)) {
                    showUpdateNotification(appContext, latestRelease.url)
                }
            } catch (exception: Exception) {
                Log.w(TAG, "检查更新失败", exception)
            }
        }.start()
    }

    private fun fetchLatestRelease(): LatestRelease {
        val json = JSONObject(fetchText(LATEST_RELEASE_API))
        val tagName = json.getString("tag_name")
        val url = json.optString("html_url", RELEASES_LATEST_URL)
            .ifBlank { RELEASES_LATEST_URL }

        return LatestRelease(tagName, url)
    }

    private fun fetchVersionCode(tagName: String): Int {
        val gradleUrl =
            "https://raw.githubusercontent.com/juren233/Yila-NFC-Helper/$tagName/app/build.gradle.kts"
        val gradleFile = fetchText(gradleUrl)
        val match = Regex("""versionCode\s*=\s*(\d+)""").find(gradleFile)
            ?: throw IllegalStateException("未找到远端 versionCode")

        return match.groupValues[1].toInt()
    }

    private fun fetchText(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 5_000
            readTimeout = 5_000
            requestMethod = "GET"
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "Yila-NFC-Helper")
        }

        return try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                throw IllegalStateException("请求失败: HTTP $responseCode")
            }

            connection.inputStream.bufferedReader().use { reader -> reader.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun showUpdateNotification(context: Context, releaseUrl: String) {
        if (!canPostNotifications(context)) {
            return
        }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        ensureNotificationChannel(context, notificationManager)

        val releaseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(releaseUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            releaseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = android.app.Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_update_title))
            .setContentText(context.getString(R.string.notification_update_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun ensureNotificationChannel(
        context: Context,
        notificationManager: NotificationManager
    ) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_update_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun canPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun currentVersionCode(context: Context): Long {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }

    private data class LatestRelease(
        val tagName: String,
        val url: String
    )
}
