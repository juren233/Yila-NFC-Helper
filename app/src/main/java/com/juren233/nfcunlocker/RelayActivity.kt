package com.juren233.nfcunlocker

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class RelayActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val unlockIntent = Intent("android.nfc.action.TAG_DISCOVERED").apply {
                component = ComponentName(
                    "com.macronum.bledemo",
                    "com.macronum.bledemo.MainActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(unlockIntent)
        } catch (exception: Exception) {
            Toast.makeText(
                this,
                getString(R.string.toast_launch_yila_failed, exception.message.orEmpty()),
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}
