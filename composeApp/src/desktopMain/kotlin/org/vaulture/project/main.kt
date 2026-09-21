package org.vaulture.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.vaulture.project.App
import org.vaulture.project.core.network.initializeFirebase

fun main() {
    initializeFirebase()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "AgriPulse - Farmer Climate & Crop Resilience",
        ) {
            App()
        }
    }
}
