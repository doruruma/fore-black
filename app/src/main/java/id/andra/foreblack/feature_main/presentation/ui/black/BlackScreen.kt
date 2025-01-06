package id.andra.foreblack.feature_main.presentation.ui.black

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.andra.foreblack.feature_main.service.OverlayService

@Preview
@Composable
private fun Preview() {
    BlackScreen()
}

@Composable
fun BlackScreen() {

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .clickable {
                Intent(context, OverlayService::class.java).apply {
                    action = OverlayService.ACTION_START
                    putExtra("VISIBILITY", OverlayService.HIDE_OVERLAY)
                    context.startService(this)
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
            Text(
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                text = "Fore Black is currently running"
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.W300,
                    fontSize = 14.sp
                ),
                text = "tap on the screen to close"
            )
        }
    }

}