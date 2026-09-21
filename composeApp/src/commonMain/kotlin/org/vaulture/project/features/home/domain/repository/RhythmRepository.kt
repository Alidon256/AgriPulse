package org.vaulture.project.features.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.features.home.domain.model.RhythmTrack

interface RhythmRepository {
    fun getTracksStream(): Flow<List<RhythmTrack>>
}
