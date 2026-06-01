package com.juren233.nfcunlocker

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.widget.Toast

class SetupActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            val shouldOpenPreference = try {
                nfcAdapter != null && !nfcAdapter.isTagIntentAllowed
            } catch (exception: SecurityException) {
                false
            }

            if (shouldOpenPreference) {
                Toast.makeText(this, getString(R.string.toast_allow_nfc_tag), Toast.LENGTH_LONG).show()
                startActivity(Intent(NfcAdapter.ACTION_CHANGE_TAG_INTENT_PREFERENCE))
                finish()
                return
            }
        }

        Toast.makeText(this, getString(R.string.toast_ready), Toast.LENGTH_LONG).show()
        finish()
    }
}
