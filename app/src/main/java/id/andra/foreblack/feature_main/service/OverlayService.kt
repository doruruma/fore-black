package id.andra.foreblack.feature_main.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import id.andra.foreblack.feature_main.presentation.ui.black.BlackScreen
import id.andra.foreblack.feature_main.util.ServiceLifecycleOwner
import id.andra.foreblack.feature_main.util.ServiceSavedStateRegistryOwner
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : Service() {

    @Inject
    lateinit var serviceLifecycleOwner: ServiceLifecycleOwner

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var savedStateRegistryOwner: ServiceSavedStateRegistryOwner

    private val CHANNEL_ID = "overlay_channel_id"
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
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(serviceLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
            setContent {
                BlackScreen()
            }
        }
        // Set the layout parameters for the overlay
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }
        // Add the view to the window
        windowManager.addView(composeView, layoutParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        // Create the overlay and the notification
        createNotification()
        // Make the service a foreground service with a notification
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun stop() {
        windowManager.removeView(composeView)
        stopForeground(STOP_FOREGROUND_REMOVE)
        savedStateRegistryOwner.stop()
        serviceLifecycleOwner.stop()
        stopSelf()
    }

    private fun createNotification(): Notification {
        // Create Notification Channel for devices running Android O and above
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Overlay Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        // Create a PendingIntent that will launch the service to show the overlay
        val overlayIntent = Intent(this, OverlayService::class.java).apply {
            action = "SHOW_OVERLAY"
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            overlayIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Overlay Service")
            .setContentText("Tap to show black screen overlay")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

}