package org.vaulture.project.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get

@Composable
internal actual fun VideoPicker(
    show: Boolean,
    onVideoSelected: (videoBytes: ByteArray?, metadata: VideoMetadata?) -> Unit
) {
    LaunchedEffect(show) {
        if (show) {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = "video/*"

            input.onchange = { event ->
                val file = (event.target as? HTMLInputElement)?.files?.get(0)
                if (file != null) {
                    val reader = FileReader()
                    reader.onload = { loadEvent ->
                        val arrayBuffer = loadEvent.target.asDynamic().result as? ArrayBuffer
                        if (arrayBuffer != null) {
                            val bytes = Int8Array(arrayBuffer).unsafeCast<ByteArray>()
                            val metadata = VideoMetadata(
                                thumbnailBytes = null,
                                durationText = "05:00",
                                durationMs = 300000L,
                                aspectRatio = 1.777f,
                                fileName = file.name
                            )
                            onVideoSelected(bytes, metadata)
                        } else {
                            onVideoSelected(null, null)
                        }
                    }
                    reader.readAsArrayBuffer(file)
                } else {
                    onVideoSelected(null, null)
                }
            }

            input.click()
        }
    }
}
