package org.vaulture.project.features.home.domain.repository

import org.vaulture.project.features.home.domain.model.CheckInEntry

interface AgronomicActionRepository {
    suspend fun saveAgronomicActionEntry(entry: CheckInEntry): Result<Unit>
    suspend fun saveCbtEntry(entry: CheckInEntry): Result<Unit> = saveAgronomicActionEntry(entry)
}

typealias CbtRepository = AgronomicActionRepository
