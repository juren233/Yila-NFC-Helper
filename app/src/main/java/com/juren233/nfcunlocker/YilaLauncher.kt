package com.juren233.nfcunlocker

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent

object YilaLauncher {
    private const val ACTION_TAG_DISCOVERED = "android.nfc.action.TAG_DISCOVERED"
    private const val YILA_PACKAGE = "com.macronum.bledemo"
    private const val YILA_ACTIVITY = "com.macronum.bledemo.MainActivity"
    private const val REQUEST_UNLOCK = 233

    fun createUnlockIntent(): Intent =
        Intent(ACTION_TAG_DISCOVERED).apply {
            component = ComponentName(YILA_PACKAGE, YILA_ACTIVITY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    fun createUnlockPendingIntent(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            REQUEST_UNLOCK,
            createUnlockIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun launch(context: Context) {
        context.startActivity(createUnlockIntent())
    }
}
