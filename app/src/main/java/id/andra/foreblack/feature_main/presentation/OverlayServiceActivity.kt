package id.andra.foreblack.feature_main.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import id.andra.foreblack.ForeBlack
import id.andra.foreblack.feature_main.service.OverlayService

class OverlayServiceActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!(application as ForeBlack).isAppOnForeground())
            moveTaskToBack(true)
        val intent = Intent(this, OverlayService::class.java).apply {
            action = OverlayService.ACTION_START
            putExtra("VISIBILITY", OverlayService.SHOW_OVERLAY)
        }
        startService(intent)
        finish()
    }

}