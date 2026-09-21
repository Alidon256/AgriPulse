package org.vaulture.project.features.home.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.vaulture.project.features.home.data.DEFAULT_TRACKS
import org.vaulture.project.features.home.domain.model.RhythmTrack
import org.vaulture.project.features.home.domain.repository.RhythmRepository

class RhythmRepositoryImpl(
    private val firestore: FirebaseFirestore
) : RhythmRepository {

    override fun getTracksStream(): Flow<List<RhythmTrack>> = firestore
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
}
