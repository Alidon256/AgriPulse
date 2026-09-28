package org.vaulture.project.features.home.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.vaulture.project.features.home.domain.model.DEFAULT_VIDEO_GUIDES
import org.vaulture.project.features.home.domain.model.FarmerVideoGuide
import org.vaulture.project.features.home.domain.repository.VideoGuideRepository

class VideoGuideRepositoryImpl(
    private val firestore: FirebaseFirestore
) : VideoGuideRepository {

    private val _localGuides = MutableStateFlow<List<FarmerVideoGuide>>(emptyList())

    override fun getVideoGuidesStream(): Flow<List<FarmerVideoGuide>> {
        val remoteFlow = firestore
            .collection("video_guides")
            .snapshots
            .map { snapshot ->
                val remoteList = snapshot.documents.mapNotNull { doc ->
                    try {
                        val guide: FarmerVideoGuide = doc.data()
                        guide.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (remoteList.isEmpty()) DEFAULT_VIDEO_GUIDES else remoteList
            }
            .catch {
                emit(DEFAULT_VIDEO_GUIDES)
            }

        return combine(remoteFlow, _localGuides.asStateFlow()) { remote, local ->
            // Local newly created guides appear first, followed by remote, avoiding duplicates
            val localIds = local.map { it.id }.toSet()
            local + remote.filterNot { it.id in localIds }
        }
    }

    override suspend fun addVideoGuide(guide: FarmerVideoGuide) {
        _localGuides.value = listOf(guide) + _localGuides.value
        try {
            firestore.collection("video_guides").document(guide.id).set(guide)
        } catch (e: Exception) {
            println("Offline or failed to sync video guide to firestore: ${e.message}")
        }
    }
}
