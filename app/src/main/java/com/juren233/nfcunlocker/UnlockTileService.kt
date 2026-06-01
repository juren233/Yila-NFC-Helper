package com.juren233.nfcunlocker

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

class UnlockTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()

        qsTile?.apply {
            label = getString(R.string.tile_unlock_label)
            icon = Icon.createWithResource(this@UnlockTileService, R.drawable.ic_material_lock_open_24)
            state = Tile.STATE_ACTIVE
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()

        val launchAction = Runnable { launchYilaDoor() }
        if (isLocked) {
            unlockAndRun(launchAction)
        } else {
            launchAction.run()
        }
    }

    private fun launchYilaDoor() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(YilaLauncher.createUnlockPendingIntent(this))
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(YilaLauncher.createUnlockIntent())
            }
        } catch (exception: Exception) {
            Toast.makeText(
                this,
                getString(R.string.toast_launch_yila_failed, exception.message.orEmpty()),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
