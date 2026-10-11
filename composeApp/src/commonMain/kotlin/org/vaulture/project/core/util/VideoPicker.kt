package org.vaulture.project.core.util

import androidx.compose.runtime.Composable

data class VideoMetadata(
    val thumbnailBytes: ByteArray? = null,
    val durationText: String = "00:00",
    val durationMs: Long = 0L,
    val aspectRatio: Float = 1.777f,
    val fileName: String = "video.mp4",
    val localUri: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as VideoMetadata
        if (durationMs != other.durationMs) return false
        if (durationText != other.durationText) return false
        if (aspectRatio != other.aspectRatio) return false
        if (fileName != other.fileName) return false
        if (localUri != other.localUri) return false
        if (thumbnailBytes != null) {
            if (other.thumbnailBytes == null) return false
            if (!thumbnailBytes.contentEquals(other.thumbnailBytes)) return false
        } else if (other.thumbnailBytes != null) return false
        return true
    }

    override fun hashCode(): Int {
        var result = thumbnailBytes?.contentHashCode() ?: 0
        result = 31 * result + durationText.hashCode()
        result = 31 * result + durationMs.hashCode()
        result = 31 * result + aspectRatio.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + (localUri?.hashCode() ?: 0)
        return result
    }
}

/**
 * Universal video picker with automatic frame thumbnail and metadata extraction.
 */
@Composable
internal expect fun VideoPicker(
    show: Boolean,
    onVideoSelected: (videoBytes: ByteArray?, metadata: VideoMetadata?) -> Unit
)
