package id.andra.foreblack.feature_main.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import id.andra.foreblack.R
import id.andra.foreblack.feature_main.presentation.OverlayServiceActivity
import id.andra.foreblack.feature_main.presentation.ui.black.BlackScreen
import id.andra.foreblack.feature_main.util.ServiceLifecycleOwner
import id.andra.foreblack.feature_main.util.ServiceSavedStateRegistryOwner
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : Service() {

    @Inject
    lateinit var serviceLifecycleOwner: ServiceLifecycleOwner

    private lateinit var windowManager: WindowManager
    private lateinit var savedStateRegistryOwner: ServiceSavedStateRegistryOwner
    private lateinit var layoutParam: LayoutParams
    private var composeView: ComposeView? = null

    private val CHANNEL_ID = "OVERLAY_CHANNEL"
    private val NOTIFICATION_ID = 1

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // Get the WindowManager system service
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        // Initialize and start the lifecycle and saved state registry
        savedStateRegistryOwner = ServiceSavedStateRegistryOwner()
        savedStateRegistryOwner.performRestore(null)
        savedStateRegistryOwner.start()
        // Start the lifecycle
        serviceLifecycleOwner.start()
        // Create a new ComposeView
        layoutParam = LayoutParams(
            LayoutParams.TYPE_APPLICATION_OVERLAY,
            LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = windowManager.currentWindowMetrics
                height = metrics.bounds.height()
                width = metrics.bounds.width()
            } else {
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getRealMetrics(metrics)
                height = metrics.heightPixels
                width = metrics.widthPixels
            }
        }
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(serviceLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
            setContent {
                BlackScreen()
            }
        }
        // create notification channel
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Overlay Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start(intent)
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(intent: Intent?) {
        startForeground(NOTIFICATION_ID, createNotification())
        val visibility = intent?.getStringExtra("VISIBILITY")
        when (visibility) {
            SHOW_OVERLAY -> {
                if (composeView?.isAttachedToWindow == false)
                    windowManager.addView(composeView, layoutParam)
            }

            HIDE_OVERLAY -> {
                if (composeView != null && composeView?.isAttachedToWindow == true)
                    windowManager.removeView(composeView)
            }
        }
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        savedStateRegistryOwner.stop()
        serviceLifecycleOwner.stop()
        if (composeView != null && composeView?.isAttachedToWindow == true)
            windowManager.removeView(composeView)
        composeView = null
    }

    private fun createNotification(): Notification {
        val overlayIntent = Intent(this, OverlayServiceActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            overlayIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val overlayIntentAction = Intent(this, OverlayService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingIntentAction = PendingIntent.getService(
            this,
            0,
            overlayIntentAction,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentTitle("Fore Black")
            .setContentText("Tap to toggle black screen overlay")
            .setSmallIcon(R.mipmap.ic_launcher_new_round)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher_new_round, "Stop Fore Black", pendingIntentAction)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val SHOW_OVERLAY = "SHOW_OVERLAY"
        const val HIDE_OVERLAY = "HIDE_OVERLAY"
    }

}