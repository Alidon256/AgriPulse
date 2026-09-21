package org.vaulture.project.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow

class NoOpAudioPlayer : KmpAudioPlayer {
    override val isPlaying = MutableStateFlow(false)
    override val currentPosition = MutableStateFlow(0L)
    override val duration = MutableStateFlow(0L)
    override fun play(url: String, title: String, artist: String) {}
    override fun pause() {}
    override fun resume() {}
    override fun stop() {}
    override fun seekTo(positionMs: Long) {}
}

actual fun createAudioPlayer(): KmpAudioPlayer = NoOpAudioPlayer()

@Composable
actual fun rememberKmpAudioPlayer(): KmpAudioPlayer {
    val context = LocalContext.current
    val player = remember { AndroidAudioPlayer(context) }

    LaunchedEffect(player) {
        player.updateProgress()
    }

    DisposableEffect(Unit) {
        onDispose { player.stop() }
    }

    return player
}
