package org.vaulture.project.features.auth.data

import dev.gitlive.firebase.auth.GoogleAuthProvider

internal actual suspend fun AuthServiceImpl.performGoogleSignIn() {
    // Google Sign-In on Android is typically handled via Activity result launchers.
    // In this app, it is triggered via the onGoogleSignInRequest callback in App.kt,
    // which calls MainActivity.launchGoogleSignIn().
}

internal actual suspend fun AuthServiceImpl.signInWithGoogleIdToken(idToken: String) {
    val credential = GoogleAuthProvider.credential(idToken, null)
    auth.signInWithCredential(credential)
}
