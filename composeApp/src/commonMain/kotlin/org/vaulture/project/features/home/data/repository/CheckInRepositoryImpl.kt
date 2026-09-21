package org.vaulture.project.features.home.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.vaulture.project.features.home.domain.model.CheckInEntry
import org.vaulture.project.features.home.domain.repository.CheckInRepository

class CheckInRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CheckInRepository {

    override fun getLatestCheckInStream(): Flow<CheckInEntry?> {
        val uid = auth.currentUser?.uid ?: return flowOf(null)
        return firestore.collection("users").document(uid)
            .collection("checkIns")
            .orderBy("timestamp", Direction.DESCENDING)
            .limit(1)
            .snapshots
            .map { snapshot ->
                snapshot.documents.firstOrNull()?.let { doc ->
                    try {
                        doc.data<CheckInEntry>()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override suspend fun saveCheckInEntry(entry: CheckInEntry): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        firestore.collection("users").document(uid)
            .collection("checkIns")
            .add(entry)
    }

    override fun getDailyQuestions(): List<String> = listOf(
        "How frequently have you felt overwhelmed or stressed in the last few days?",
        "How would you rate your sleep quality and energy levels recently?",
        "Have you experienced difficulty concentrating or making decisions?",
        "How often do you feel supported by your friends, family, or community?"
    )
}
