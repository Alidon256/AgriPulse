package org.vaulture.project.features.auth.domain

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.core.domain.User

interface AuthService {
    val isAuthenticated: Flow<Boolean>
    val currentUser: Flow<User?>
    suspend fun createAuthUser(email: String, password: String): String
    suspend fun createUserProfile(
        uid: String,
        email: String,
        username: String,
        profilePicture: ByteArray?,
        farmName: String? = null,
        country: String? = null,
        region: String? = null,
        farmSize: String? = null,
        primaryCrops: List<String> = emptyList(),
        farmingType: String? = null,
        irrigationType: String? = null,
        soilType: String? = null,
        experienceLevel: String? = null,
        certifications: List<String> = emptyList()
    )
    suspend fun onSignInSuccess(): Boolean
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun signInWithGoogle()
    suspend fun signOut()
}