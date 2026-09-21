package org.vaulture.project.core.network

import dev.gitlive.firebase.FirebaseOptions

expect fun initializeFirebase(context: Any? = null)

// Shared configuration for Web/Desktop (Android uses google-services.json)
val firebaseOptions = FirebaseOptions(
    applicationId = "1:764343613928:web:6dc5aeff7245af51a30c5d",
    apiKey = "AIzaSyAorYwOFIYfU4vjC_VTtrV8ODbrxvy1bEw",
    projectId = "mindsetpulse-one",
    storageBucket = "mindsetpulse-one.firebasestorage.app",
    authDomain = "mindsetpulse-one.firebaseapp.com"
)
