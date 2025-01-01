package id.andra.foreblack.feature_main.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import id.andra.foreblack.feature_main.service.OverlayService

class OverlayTriggerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, OverlayService::class.java)
        startService(intent)
        finish()
    }

}