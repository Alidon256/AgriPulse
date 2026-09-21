package org.vaulture.project.features.home.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import org.vaulture.project.features.home.domain.model.CheckInEntry
import org.vaulture.project.features.home.domain.repository.AgronomicActionRepository

class AgronomicActionRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AgronomicActionRepository {

    override suspend fun saveAgronomicActionEntry(entry: CheckInEntry): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        firestore.collection("users").document(uid)
            .collection("cbts") // Preserves existing collection data
            .add(entry)
    }
}

typealias CbtRepositoryImpl = AgronomicActionRepositoryImpl
