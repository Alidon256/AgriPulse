package org.vaulture.project.features.wellness.domain.repository

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.features.wellness.domain.model.WellnessRecord
import org.vaulture.project.features.wellness.domain.model.WellnessStats

interface WellnessRepository {
    fun getWellnessStatsStream(): Flow<WellnessStats>
    suspend fun saveWellnessSession(record: WellnessRecord, updatedStats: WellnessStats): Result<Unit>
}
