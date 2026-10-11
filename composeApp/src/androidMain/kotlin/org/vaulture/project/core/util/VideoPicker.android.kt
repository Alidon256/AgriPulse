package org.vaulture.project.core.util

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

@Composable
internal actual fun VideoPicker(
    show: Boolean,
    onVideoSelected: (videoBytes: ByteArray?, metadata: VideoMetadata?) -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }

                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, uri)

                    val frameBitmap = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        ?: retriever.frameAtTime

                    val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                    val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toFloatOrNull() ?: 1920f
                    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toFloatOrNull() ?: 1080f
                    val ratio = if (height > 0f) width / height else 1.777f

                    val totalSec = durationMs / 1000
                    val min = totalSec / 60
                    val sec = totalSec % 60
                    val durationText = "${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}"

                    val thumbnailBytes = frameBitmap?.let { bmp ->
                        val baos = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                        baos.toByteArray()
                    }

                    retriever.release()

                    val metadata = VideoMetadata(
                        thumbnailBytes = thumbnailBytes,
                        durationText = durationText,
                        durationMs = durationMs,
                        aspectRatio = ratio,
                        fileName = "video_${System.currentTimeMillis()}.mp4",
                        localUri = uri.toString()
                    )

                    onVideoSelected(bytes, metadata)
                } catch (e: Exception) {
                    println("Error extracting video metadata: ${e.message}")
                    onVideoSelected(null, null)
                }
            } else {
                onVideoSelected(null, null)
            }
        }
    )

    LaunchedEffect(show) {
        if (show) {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }
    }
}
