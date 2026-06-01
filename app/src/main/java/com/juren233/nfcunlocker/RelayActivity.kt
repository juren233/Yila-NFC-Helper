package com.juren233.nfcunlocker

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class RelayActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            YilaLauncher.launch(this)
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
