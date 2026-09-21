package org.vaulture.project.core.domain

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class User(
    val uid: String = "",
    val displayName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val isAnonymous: Boolean = false,
    val photoUrl: String? = null,
    // Agricultural Profile & Farmer Credentials
    val farmName: String? = null,
    val country: String? = null,
    val region: String? = null,
    val farmSize: String? = null,
    val primaryCrops: List<String> = emptyList(),
    val farmingType: String? = null,
    val irrigationType: String? = null,
    val soilType: String? = null,
    val experienceLevel: String? = null,
    val certifications: List<String> = emptyList(),
    val bio: String? = null,
    val mainChallenges: List<String> = emptyList()
) {
    val effectiveName: String
        get() = displayName?.ifBlank { null }
            ?: username?.ifBlank { null }
            ?: email?.substringBefore("@")?.ifBlank { null }
            ?: "Guest User"
}

