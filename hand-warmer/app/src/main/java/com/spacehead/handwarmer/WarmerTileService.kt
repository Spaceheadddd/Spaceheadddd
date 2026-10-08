package com.spacehead.handwarmer

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Quick Settings tile. Tapping it while warming stops the heater; tapping it while
 * idle opens the app and starts with the saved settings (Android requires a visible
 * activity to launch a foreground service from a tile).
 */
class WarmerTileService : TileService() {

    companion object {
        const val EXTRA_AUTOSTART = "autostart"

        fun refresh(context: Context) {
            requestListeningState(context, ComponentName(context, WarmerTileService::class.java))
        }
    }

    override fun onStartListening() {
        val tile = qsTile ?: return
        tile.state = if (HeaterService.isRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.subtitle = if (HeaterService.isRunning) "%.0f°C".format(HeaterService.targetC) else "Off"
        tile.updateTile()
    }

    override fun onClick() {
        if (HeaterService.isRunning) {
            startService(Intent(this, HeaterService::class.java).setAction(HeaterService.ACTION_STOP))
            return
        }
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(EXTRA_AUTOSTART, true)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(
                PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            )
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
