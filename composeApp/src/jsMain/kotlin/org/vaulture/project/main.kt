package org.vaulture.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady
import org.vaulture.project.App
import org.vaulture.project.core.network.initializeFirebase

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeFirebase()

    onWasmReady {
        val body = document.body ?: return@onWasmReady
        document.title = "AgriPulse"
        ComposeViewport(viewportContainer = body) {
            App()
        }
    }
}
