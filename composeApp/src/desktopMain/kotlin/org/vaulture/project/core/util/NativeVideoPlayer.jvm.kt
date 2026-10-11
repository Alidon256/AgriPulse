package org.vaulture.project.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vaulture.project.core.theme.PoppinsTypography
import java.awt.Desktop
import java.net.URI

private fun extractYouTubeId(url: String): String? {
    if (url.isBlank()) return null
    val trimmed = url.trim()
    val pattern = Regex(
        "(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})",
        RegexOption.IGNORE_CASE
    )
    val match = pattern.find(trimmed)
    if (match != null) return match.groupValues[1]
    if (trimmed.length == 11 && trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
        return trimmed
    }
    return null
}

@Composable
actual fun NativeVideoPlayer(
    url: String,
    modifier: Modifier,
    autoPlay: Boolean
) {
    val cleanUrl = url.trim()
    val youtubeId = remember(cleanUrl) { extractYouTubeId(cleanUrl) }
    val playableUrl = if (youtubeId != null) "https://www.youtube.com/watch?v=$youtubeId" else cleanUrl

    val openVideo: () -> Unit = {
        try {
            if (playableUrl.isNotBlank() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(playableUrl))
            }
        } catch (e: Exception) {
            println("Failed to launch video on Desktop: ${e.message}")
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFF0B1320))
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (youtubeId != null) "YouTube Video" else "Video Stream",
                            style = PoppinsTypography().labelSmall,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = openVideo,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = "Open Stream",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Center Play Button & Instructions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable { openVideo() }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Play Video",
                    style = PoppinsTypography().titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Click to play stream in system media player",
                    style = PoppinsTypography().bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }

            // Bottom action button
            Button(
                onClick = openVideo,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Icon(Icons.Default.PlayCircle, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Watch Stream", style = PoppinsTypography().labelMedium)
            }
        }
    }
}
