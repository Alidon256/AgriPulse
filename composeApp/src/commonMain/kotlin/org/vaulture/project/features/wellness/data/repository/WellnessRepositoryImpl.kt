package org.vaulture.project.features.wellness.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.vaulture.project.features.wellness.domain.model.WellnessRecord
import org.vaulture.project.features.wellness.domain.model.WellnessStats
import org.vaulture.project.features.wellness.domain.repository.WellnessRepository

class WellnessRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : WellnessRepository {

    override fun getWellnessStatsStream(): Flow<WellnessStats> {
        val uid = auth.currentUser?.uid ?: return flowOf(WellnessStats())
        return firestore.collection("users").document(uid)
            .collection("stats").document("wellness")
            .snapshots
            .map { doc ->
                if (doc.exists) {
                    try {
                        doc.data<WellnessStats>()
                    } catch (e: Exception) {
                        WellnessStats()
                    }
                } else {
                    WellnessStats()
                }
            }
    }

    override suspend fun saveWellnessSession(
        record: WellnessRecord,
        updatedStats: WellnessStats
    ): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        val userDoc = firestore.collection("users").document(uid)

        userDoc.collection("wellnessRecords").add(record)
        userDoc.collection("stats").document("wellness").set(updatedStats)
    }
}
