package org.vaulture.project.features.home.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.vaulture.project.data.remote.GeminiService
import org.vaulture.project.features.home.domain.model.CheckInEntry
import org.vaulture.project.features.home.domain.repository.AnalyticsRepository

class AnalyticsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val geminiService: GeminiService
) : AnalyticsRepository {

    override fun getCheckInsStream(): Flow<List<CheckInEntry>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())
        return firestore.collection("users").document(uid)
            .collection("cbts")
            .orderBy("timestamp", Direction.DESCENDING)
            .limit(30)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.data<CheckInEntry>()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override suspend fun getAiAnalyticsReport(checkIns: List<CheckInEntry>): Result<String> = runCatching {
        geminiService.generateAnalyticsReport(checkIns)
    }
}
