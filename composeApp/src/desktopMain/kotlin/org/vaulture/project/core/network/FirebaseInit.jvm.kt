package org.vaulture.project.core.network

import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

actual fun initializeFirebase(context: Any?) {
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        private val storage = mutableMapOf<String, String>()
        override fun store(key: String, value: String) { storage[key] = value }
        override fun retrieve(key: String): String? = storage[key]
        override fun clear(key: String) { storage.remove(key) }
        override fun log(msg: String) { println("[Firebase] $msg") }
    })

    // On Desktop, the GitLive SDK internally casts the context to android.app.Application.
    // We provide a dummy instance of the library's own Application stub to avoid the ClassCastException.
    Firebase.initialize(context = android.app.Application(), options = firebaseOptions)
}
