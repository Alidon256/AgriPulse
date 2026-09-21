package org.vaulture.project.features.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.features.home.domain.model.CheckInEntry

interface AnalyticsRepository {
    fun getCheckInsStream(): Flow<List<CheckInEntry>>
    suspend fun getAiAnalyticsReport(checkIns: List<CheckInEntry>): Result<String>
}
