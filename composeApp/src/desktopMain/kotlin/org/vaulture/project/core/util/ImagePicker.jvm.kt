package org.vaulture.project.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
internal actual fun ImagePicker(
    show: Boolean,
    onImageSelected: (imageData: ByteArray?) -> Unit,
) {
    LaunchedEffect(show) {
        if (show) {
            val fileDialog = FileDialog(null as Frame?, "Select Image", FileDialog.LOAD)
            fileDialog.file = "*.jpg;*.jpeg;*.png"
            fileDialog.isVisible = true

            val directory = fileDialog.directory
            val file = fileDialog.file

            if (directory != null && file != null) {
                val selectedFile = File(directory, file)
                try {
                    val bytes = selectedFile.readBytes()
                    onImageSelected(bytes)
                } catch (e: Exception) {
                    println("Error reading file: ${e.message}")
                    onImageSelected(null)
                }
            } else {
                onImageSelected(null)
            }
        }
    }
}
