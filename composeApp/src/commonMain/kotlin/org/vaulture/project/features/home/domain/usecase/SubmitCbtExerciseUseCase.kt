package org.vaulture.project.features.home.domain.usecase

import org.vaulture.project.features.home.domain.model.CheckInEntry
import org.vaulture.project.features.home.domain.repository.AgronomicActionRepository

class SubmitAgronomicActionUseCase(
    private val agronomicActionRepository: AgronomicActionRepository
) {
    suspend operator fun invoke(entry: CheckInEntry): Result<Unit> {
        return agronomicActionRepository.saveAgronomicActionEntry(entry)
    }
}

typealias SubmitCbtExerciseUseCase = SubmitAgronomicActionUseCase
