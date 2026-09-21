package org.vaulture.project.core.util

import dev.gitlive.firebase.storage.StorageReference

internal expect suspend fun StorageReference.upload(bytes: ByteArray)
