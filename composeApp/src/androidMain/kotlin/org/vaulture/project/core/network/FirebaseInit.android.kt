package org.vaulture.project.core.network

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

actual fun initializeFirebase(context: Any?) {
    val androidContext = context as? Context ?: throw IllegalArgumentException("Android context required")
    
    Firebase.initialize(
        androidContext,
        options = FirebaseOptions(
            applicationId = "1:764343613928:android:bebcb706c855f590a30c5d",
            apiKey = "AIzaSyBtBQyWB7iVtme1eAQt9qC4ycU0K5Jmsjk",
            projectId = "mindsetpulse-one",
            storageBucket = "mindsetpulse-one.firebasestorage.app"
        )
    )
}
