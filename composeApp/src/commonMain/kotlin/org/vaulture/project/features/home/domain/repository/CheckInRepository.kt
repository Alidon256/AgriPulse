package org.vaulture.project.features.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.features.home.domain.model.CheckInEntry

interface CheckInRepository {
    fun getLatestCheckInStream(): Flow<CheckInEntry?>
    suspend fun saveCheckInEntry(entry: CheckInEntry): Result<Unit>
    fun getDailyQuestions(): List<String>
}
