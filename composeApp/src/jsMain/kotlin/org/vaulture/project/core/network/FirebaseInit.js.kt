package org.vaulture.project.core.network

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

actual fun initializeFirebase(context: Any?) {
    Firebase.initialize(options = firebaseOptions)
}
