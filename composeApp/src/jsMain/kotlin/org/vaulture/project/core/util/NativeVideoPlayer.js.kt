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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.vaulture.project.core.theme.PoppinsTypography

@Composable
actual fun NativeVideoPlayer(
    url: String,
    modifier: Modifier,
    autoPlay: Boolean
) {
    var isPlaying by remember { mutableStateOf(autoPlay) }
    var progress by remember { mutableStateOf(0.12f) }
    var isMuted by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(500)
            progress = (progress + 0.015f).let { if (it > 1f) 0f else it }
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFF0B1320))
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
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
                                .background(if (isPlaying) Color(0xFF4CAF50) else Color(0xFFFF9800))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "Streaming In-App" else "Paused",
                            style = PoppinsTypography().labelSmall,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = { isMuted = !isMuted },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Mute Toggle",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Center Play/Pause button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(54.dp)
                        .clickable { isPlaying = !isPlaying }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth().height(20.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentSec = (progress * 320).toInt()
                    val totalSec = 320
                    val currentMinStr = "${(currentSec / 60).toString().padStart(2, '0')}:${(currentSec % 60).toString().padStart(2, '0')}"
                    val totalMinStr = "${(totalSec / 60).toString().padStart(2, '0')}:${(totalSec % 60).toString().padStart(2, '0')}"

                    Text(
                        text = "$currentMinStr / $totalMinStr",
                        style = PoppinsTypography().labelSmall,
                        color = Color.White,
                        fontSize = 11.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { progress = (progress - 0.05f).coerceAtLeast(0f) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Replay, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { progress = (progress + 0.05f).coerceAtMost(1f) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.FastForward, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
