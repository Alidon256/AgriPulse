package org.vaulture.project.core.util

import dev.gitlive.firebase.storage.Data
import dev.gitlive.firebase.storage.StorageReference

internal actual suspend fun StorageReference.upload(bytes: ByteArray) {
    this.putData(Data(bytes))
}
