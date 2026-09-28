package org.vaulture.project.features.home.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import org.vaulture.project.features.home.data.DEFAULT_TRACKS
import org.vaulture.project.features.home.domain.model.RhythmTrack
import org.vaulture.project.features.home.domain.repository.RhythmRepository

class RhythmRepositoryImpl(
    private val firestore: FirebaseFirestore
) : RhythmRepository {

    private val _localTracks = MutableStateFlow<List<RhythmTrack>>(emptyList())

    override fun getTracksStream(): Flow<List<RhythmTrack>> {
        val remoteFlow = firestore
            .collection("tracks")
            .snapshots
            .map { snapshot ->
                val remoteTracks = snapshot.documents.mapNotNull { doc ->
                    try {
                        val track: RhythmTrack = doc.data()
                        track.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (remoteTracks.isEmpty()) DEFAULT_TRACKS else remoteTracks
            }
            .catch {
                emit(DEFAULT_TRACKS)
            }

        return combine(remoteFlow, _localTracks.asStateFlow()) { remote, local ->
            val localIds = local.map { it.id }.toSet()
            local + remote.filterNot { it.id in localIds }
        }
    }

    override suspend fun addTrack(track: RhythmTrack) {
        _localTracks.value = listOf(track) + _localTracks.value
        try {
            firestore.collection("tracks").document(track.id).set(track)
        } catch (e: Exception) {
            println("Offline or failed to sync audio track to firestore: ${e.message}")
        }
    }
}
