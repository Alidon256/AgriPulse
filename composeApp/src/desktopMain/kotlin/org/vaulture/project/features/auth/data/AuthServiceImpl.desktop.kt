package org.vaulture.project.features.auth.data

internal actual suspend fun AuthServiceImpl.performGoogleSignIn() {
    println("[DESKTOP] Google Sign-In is not yet implemented for this platform.")
}

internal actual suspend fun AuthServiceImpl.signInWithGoogleIdToken(idToken: String) {
    println("[DESKTOP] Sign-In with ID Token is not yet implemented for this platform.")
}
