package org.vaulture.project.features.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.features.home.domain.model.RhythmTrack
import org.vaulture.project.features.home.presentation.viewmodel.RhythmUiState
import org.vaulture.project.features.home.presentation.viewmodel.RhythmViewModel

@Composable
fun RhythmPlayerScreen(trackId: String?, viewModel: RhythmViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val track = state.currentTrack

    LaunchedEffect(trackId) {
        if (trackId != null && state.currentTrack?.id != trackId) {
            viewModel.loadTrackById(trackId)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight
        val isWideScreen = maxWidth > 800.dp
        val isCompactHeight = maxHeight < 700.dp

        AsyncImage(
            model = track?.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.6f),
                        Color.Black.copy(alpha = 0.85f),
                        Color.Black
                    )
                )
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = if (isWideScreen) 32.dp else 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Agricultural Extension Guide",
                    style = PoppinsTypography().titleMedium,
                    color = Color.White.copy(0.85f),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.weight(0.5f))

            Box(
                modifier = Modifier
                    .widthIn(max = 580.dp)
                    .fillMaxWidth()
                    .weight(4f),
                contentAlignment = Alignment.Center
            ) {
                if (isCompactHeight) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        PlayerContent(track, state, viewModel, isCompact = true, isWideScreen = isWideScreen)
                    }
                } else {
                    PlayerContent(track, state, viewModel, isCompact = false, isWideScreen = isWideScreen)
                }
            }

            Spacer(Modifier.weight(0.5f))
        }
    }
}

@Composable
private fun PlayerContent(
    track: RhythmTrack?,
    state: RhythmUiState,
    viewModel: RhythmViewModel,
    isCompact: Boolean,
    isWideScreen: Boolean
) {
    val cardSize = when {
        isCompact -> 180.dp
        isWideScreen -> 320.dp
        else -> 260.dp
    }
    val spacerSize = if (isCompact) 16.dp else 28.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .size(cardSize)
                .aspectRatio(1f),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.elevatedCardElevation(16.dp)
        ) {
            AsyncImage(
                model = track?.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(spacerSize))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(
                text = track?.title ?: "Select a Track",
                style = if (isWideScreen) PoppinsTypography().headlineLarge else PoppinsTypography().headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = track?.artist ?: "AgriPulse Extension",
                style = PoppinsTypography().titleMedium,
                color = Color.White.copy(0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(spacerSize))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            val maxDuration = state.duration.toFloat().coerceAtLeast(1f)
            val currentProgress = state.progress.toFloat().coerceIn(0f, maxDuration)

            Slider(
                value = currentProgress,
                onValueChange = { viewModel.seekTo(it.toLong()) },
                valueRange = 0f..maxDuration,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.White.copy(0.25f)
                ),
                modifier = Modifier.fillMaxWidth().height(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatMillis(state.progress),
                    color = Color.White.copy(0.8f),
                    style = PoppinsTypography().labelMedium
                )
                Text(
                    formatMillis(state.duration),
                    color = Color.White.copy(0.8f),
                    style = PoppinsTypography().labelMedium
                )
            }
        }

        Spacer(Modifier.height(spacerSize))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            IconButton(
                onClick = { viewModel.playPrevious() },
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            FloatingActionButton(
                onClick = { viewModel.togglePlayback() },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.size(68.dp)
            ) {
                Icon(
                    if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = { viewModel.playNext() },
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

private fun formatMillis(ms: Long): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return "$mins:${secs.toString().padStart(2, '0')}"
}
