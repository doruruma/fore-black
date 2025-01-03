package id.andra.foreblack.feature_main.presentation.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import id.andra.foreblack.R
import id.andra.foreblack.feature_main.presentation.components.OnLifecycleEvent
import id.andra.foreblack.feature_main.service.OverlayService
import id.andra.foreblack.feature_main.util.hasPermission
import id.andra.foreblack.feature_main.util.isMyServiceRunning
import id.andra.foreblack.ui.theme.Neutral80
import id.andra.foreblack.ui.theme.Neutral90

@Preview
@Composable
private fun HomePreview() {
    HomeScreen()
}

@Composable
fun HomeScreen() {

    val context = LocalContext.current
    var isPermissionGranted by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }
    var notificationPermissionCallback by remember {
        mutableStateOf({})
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            notificationPermissionCallback.invoke()
            notificationPermissionCallback = {}
        }
    )

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    fun requestOverlayPermission(context: Context) {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    }

    fun getMainButtonLabel(): String {
        return if (isPermissionGranted)
            if (context.isMyServiceRunning(OverlayService::class.java)) "Disable Fore Black" else "Enable Fore Black"
        else "Go To Setting"
    }

    LaunchedEffect(true) {
        requestNotificationPermission()
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                isPermissionGranted = Settings.canDrawOverlays(context)
            }

            else -> {}
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isPermissionGranted)
                    Image(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 16.dp),
                        painter = painterResource(R.drawable.floating),
                        contentScale = ContentScale.Fit,
                        contentDescription = "notify"
                    )
                if (!isPermissionGranted)
                    Image(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 16.dp),
                        painter = painterResource(R.drawable.notify),
                        contentScale = ContentScale.Fit,
                        contentDescription = "notify"
                    )
                Text(
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Neutral90
                    ),
                    text = "Welcome to Fore Black"
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Neutral80
                    ),
                    text = if (isPermissionGranted) "You are ready to go."
                    else "Allow App to Appear on Top.\nThis permission is required for the app to function properly."
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    onClick = {
                        if (!isPermissionGranted) {
                            requestOverlayPermission(context)
                            return@Button
                        }
                        if (!context.hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
                            notificationPermissionCallback = {
                                Intent(context, OverlayService::class.java).apply {
                                    action = OverlayService.ACTION_START
                                    context.startService(this)
                                }
                            }
                            requestNotificationPermission()
                            return@Button
                        }
                        Intent(context, OverlayService::class.java).apply {
                            action = OverlayService.ACTION_START
                            context.startService(this)
                        }
                    }
                ) {
                    Text(
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        text = getMainButtonLabel()
                    )
                }
            }
        }
    }

}