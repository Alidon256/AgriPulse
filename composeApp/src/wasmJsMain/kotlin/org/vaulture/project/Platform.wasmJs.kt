package org.vaulture.project

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import org.vaulture.project.core.domain.Platform
import org.vaulture.project.core.domain.getPlatform
import org.vaulture.project.core.util.KmpAudioPlayer

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

class WasmAudioPlayer : KmpAudioPlayer {
    override val isPlaying = MutableStateFlow(false)
    override val currentPosition = MutableStateFlow(0L)
    override val duration = MutableStateFlow(0L)
    override fun play(url: String, title: String, artist: String) {}
    override fun pause() {}
    override fun resume() {}
    override fun stop() {}
    override fun seekTo(positionMs: Long) {}
}

actual fun createAudioPlayer(): KmpAudioPlayer = WasmAudioPlayer()

@Composable
actual fun rememberKmpAudioPlayer(): KmpAudioPlayer = WasmAudioPlayer()
