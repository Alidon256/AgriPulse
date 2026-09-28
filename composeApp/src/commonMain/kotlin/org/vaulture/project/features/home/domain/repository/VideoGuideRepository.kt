package org.vaulture.project.features.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.features.home.domain.model.FarmerVideoGuide

interface VideoGuideRepository {
    fun getVideoGuidesStream(): Flow<List<FarmerVideoGuide>>
    suspend fun addVideoGuide(guide: FarmerVideoGuide)
}
