package org.vaulture.project.features.auth.data

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.js // Required for the 'FirebaseAuth.js' property
import kotlinx.coroutines.await
import kotlin.js.Promise

private external interface FirebaseJsAuth {
    fun signInWithPopup(provider: dynamic): Promise<dynamic>
}

private val FirebaseAuth.jsAuth: FirebaseJsAuth
    get() = this.js.unsafeCast<FirebaseJsAuth>()

@JsModule("firebase/auth")
@JsNonModule
private external val GoogleAuthProviderModule: dynamic


internal actual suspend fun AuthServiceImpl.performGoogleSignIn() {
    val jsProvider = js("new GoogleAuthProviderModule.GoogleAuthProvider()")
    auth.jsAuth.signInWithPopup(jsProvider).await()
}

internal actual suspend fun AuthServiceImpl.signInWithGoogleIdToken(idToken: String) {
    // Google ID token sign-in is typically used on platforms where we get the token first (like Android).
    // On Web/JS, signInWithPopup is the standard flow.
}
