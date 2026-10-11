package org.vaulture.project.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
internal actual fun VideoPicker(
    show: Boolean,
    onVideoSelected: (videoBytes: ByteArray?, metadata: VideoMetadata?) -> Unit
) {
    LaunchedEffect(show) {
        if (show) {
            val fileDialog = FileDialog(null as Frame?, "Select Video", FileDialog.LOAD)
            fileDialog.file = "*.mp4;*.mkv;*.mov;*.webm"
            fileDialog.isVisible = true

            val directory = fileDialog.directory
            val file = fileDialog.file

            if (directory != null && file != null) {
                val selectedFile = File(directory, file)
                try {
                    val bytes = selectedFile.readBytes()
                    val metadata = VideoMetadata(
                        thumbnailBytes = null,
                        durationText = "05:00",
                        durationMs = 300000L,
                        aspectRatio = 1.777f,
                        fileName = file
                    )
                    onVideoSelected(bytes, metadata)
                } catch (e: Exception) {
                    println("Error reading video file on desktop: ${e.message}")
                    onVideoSelected(null, null)
                }
            } else {
                onVideoSelected(null, null)
            }
        }
    }
}
