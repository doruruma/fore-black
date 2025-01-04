package id.andra.foreblack.feature_main.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import id.andra.foreblack.feature_main.service.OverlayService

class OverlayBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, OverlayService::class.java).apply {
            action = OverlayService.ACTION_START
            putExtra("VISIBILITY", OverlayService.SHOW_OVERLAY)
        }
        context.startService(serviceIntent)
    }
}