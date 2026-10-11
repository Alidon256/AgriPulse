package org.vaulture.project.core.util

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

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

@OptIn(UnstableApi::class)
@Composable
actual fun NativeVideoPlayer(
    url: String,
    modifier: Modifier,
    autoPlay: Boolean
) {
    val cleanUrl = url.trim()
    val youtubeId = remember(cleanUrl) { extractYouTubeId(cleanUrl) }

    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (cleanUrl.isBlank()) {
            Text(
                text = "No video stream available",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
            return@Box
        }

        if (youtubeId != null) {
            // YouTube Web Player (works seamlessly in Dialogs & Compose layouts)
            var isWebLoading by remember(youtubeId) { mutableStateOf(true) }

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            loadWithOverviewMode = true
                            useWideViewPort = true
                        }
                        setBackgroundColor(0) // Transparent background
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                if (newProgress >= 90) {
                                    isWebLoading = false
                                }
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isWebLoading = false
                            }
                        }
                        val embedHtml = """
                            <!DOCTYPE html>
                            <html style="margin:0;padding:0;width:100%;height:100%;background:#000;">
                            <head>
                                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                <style>
                                    * { margin:0; padding:0; box-sizing:border-box; }
                                    body, html { width:100%; height:100%; overflow:hidden; background:#000; }
                                    iframe { width:100%; height:100%; border:none; display:block; }
                                </style>
                            </head>
                            <body>
                                <iframe 
                                    src="https://www.youtube-nocookie.com/embed/$youtubeId?autoplay=${if (autoPlay) 1 else 0}&playsinline=1&rel=0&modestbranding=1" 
                                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                                    allowfullscreen>
                                </iframe>
                            </body>
                            </html>
                        """.trimIndent()
                        loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                onRelease = { webView ->
                    webView.stopLoading()
                    webView.loadUrl("about:blank")
                    webView.destroy()
                }
            )

            if (isWebLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        } else {
            // Direct Video Stream (MP4, HLS, WebM, Firebase Storage) via ExoPlayer with TextureView
            val context = LocalContext.current
            var isBuffering by remember(cleanUrl) { mutableStateOf(true) }
            var playbackError by remember(cleanUrl) { mutableStateOf<String?>(null) }

            val exoPlayer = remember(cleanUrl) {
                ExoPlayer.Builder(context).build().apply {
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            isBuffering = playbackState == Player.STATE_BUFFERING
                            if (playbackState == Player.STATE_READY) {
                                playbackError = null
                            }
                        }

                        override fun onPlayerError(error: PlaybackException) {
                            isBuffering = false
                            playbackError = error.localizedMessage ?: "Playback error encountered"
                        }
                    })
                    setMediaItem(MediaItem.fromUri(cleanUrl))
                    prepare()
                    playWhenReady = autoPlay
                }
            }

            DisposableEffect(exoPlayer) {
                onDispose {
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = { ctx ->
                    val playerView = try {
                        (android.view.LayoutInflater.from(ctx).inflate(
                            org.vaulture.project.R.layout.view_video_player,
                            null
                        ) as PlayerView)
                    } catch (_: Throwable) {
                        PlayerView(ctx).apply {
                            useController = true
                            setShowNextButton(false)
                            setShowPreviousButton(false)
                            setShowFastForwardButton(true)
                            setShowRewindButton(true)
                        }
                    }

                    playerView.apply {
                        (videoSurfaceView as? android.view.SurfaceView)?.setZOrderMediaOverlay(true)
                        player = exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isBuffering && playbackError == null) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            if (playbackError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unable to stream video:\n$playbackError",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
