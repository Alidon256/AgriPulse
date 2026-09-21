package org.vaulture.project.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javazoom.jl.player.Player
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI

class JvmAudioPlayer : KmpAudioPlayer {
    override val isPlaying = MutableStateFlow(false)
    override val currentPosition = MutableStateFlow(0L)
    override val duration = MutableStateFlow(0L)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var playJob: Job? = null
    private var progressJob: Job? = null

    @Volatile
    private var activePlayer: Player? = null
    @Volatile
    private var activeInputStream: InputStream? = null

    private var currentUrl: String? = null
    private var startTimeMs: Long = 0L
    private var pausedPositionMs: Long = 0L

    override fun play(url: String, title: String, artist: String) {
        stopAudioInternal()

        currentUrl = url
        pausedPositionMs = 0L
        currentPosition.value = 0L

        playJob = scope.launch {
            try {
                isPlaying.value = true
                startTimeMs = System.currentTimeMillis()

                println("JVM AudioPlayer: Downloading audio buffer for '$title' ($url)...")
                val connection = URI.create(url).toURL().openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 15000

                val bytes = connection.getInputStream().use { it.readBytes() }
                if (!isActive) return@launch

                println("JVM AudioPlayer: Downloaded ${bytes.size} bytes. Initializing Player...")
                val byteArrayStream = ByteArrayInputStream(bytes)
                activeInputStream = byteArrayStream

                val jlPlayer = Player(byteArrayStream)
                activePlayer = jlPlayer

                progressJob = scope.launch {
                    while (isActive && isPlaying.value) {
                        val elapsed = System.currentTimeMillis() - startTimeMs + pausedPositionMs
                        currentPosition.value = elapsed
                        delay(200)
                    }
                }

                println("JVM AudioPlayer: Starting playback for '$title'...")
                jlPlayer.play()

                if (isActive) {
                    withContext(Dispatchers.Main) {
                        isPlaying.value = false
                        currentPosition.value = 0L
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    println("JVM AudioPlayer Exception: ${e.message}")
                }
            } finally {
                progressJob?.cancel()
            }
        }
    }

    override fun pause() {
        if (!isPlaying.value) return
        pausedPositionMs = currentPosition.value
        isPlaying.value = false
        stopAudioInternal()
    }

    override fun resume() {
        val url = currentUrl ?: return
        if (isPlaying.value) return
        play(url, "", "")
    }

    override fun stop() {
        isPlaying.value = false
        pausedPositionMs = 0L
        currentPosition.value = 0L
        stopAudioInternal()
    }

    private fun stopAudioInternal() {
        progressJob?.cancel()
        progressJob = null

        val p = activePlayer
        activePlayer = null
        try {
            p?.close()
        } catch (e: Exception) {
            println("JVM AudioPlayer close error: ${e.message}")
        }

        val s = activeInputStream
        activeInputStream = null
        try {
            s?.close()
        } catch (e: Exception) {
            println("JVM AudioPlayer stream close error: ${e.message}")
        }

        playJob?.cancel()
        playJob = null
    }

    override fun seekTo(positionMs: Long) {
        pausedPositionMs = positionMs
        currentPosition.value = positionMs
    }

    fun cleanup() {
        stop()
        scope.cancel()
    }
}

actual fun createAudioPlayer(): KmpAudioPlayer = JvmAudioPlayer()

@Composable
actual fun rememberKmpAudioPlayer(): KmpAudioPlayer {
    val player = remember { JvmAudioPlayer() }
    DisposableEffect(player) {
        onDispose {
            player.cleanup()
        }
    }
    return player
}
