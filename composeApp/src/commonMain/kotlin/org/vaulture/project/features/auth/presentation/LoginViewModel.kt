package org.vaulture.project.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.vaulture.project.features.auth.domain.AuthService

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val profilePicture: ByteArray? = null,
    val farmName: String = "",
    val country: String = "Kenya",
    val region: String = "",
    val farmSize: String = "1 - 3 Acres",
    val primaryCrops: List<String> = listOf("Maize", "Beans"),
    val farmingType: String = "Smallholder Mixed",
    val irrigationType: String = "Rain-Fed",
    val soilType: String = "Loam",
    val experienceLevel: String = "Practicing (3-5 yrs)",
    val certifications: List<String> = emptyList(),
    val signUpStep: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LoginUiState

        if (isLoading != other.isLoading) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (username != other.username) return false
        if (farmName != other.farmName) return false
        if (country != other.country) return false
        if (region != other.region) return false
        if (farmSize != other.farmSize) return false
        if (primaryCrops != other.primaryCrops) return false
        if (farmingType != other.farmingType) return false
        if (irrigationType != other.irrigationType) return false
        if (soilType != other.soilType) return false
        if (experienceLevel != other.experienceLevel) return false
        if (certifications != other.certifications) return false
        if (signUpStep != other.signUpStep) return false
        if (!profilePicture.contentEquals(other.profilePicture)) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + farmName.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + region.hashCode()
        result = 31 * result + farmSize.hashCode()
        result = 31 * result + primaryCrops.hashCode()
        result = 31 * result + farmingType.hashCode()
        result = 31 * result + irrigationType.hashCode()
        result = 31 * result + soilType.hashCode()
        result = 31 * result + experienceLevel.hashCode()
        result = 31 * result + certifications.hashCode()
        result = 31 * result + signUpStep.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}

class LoginViewModel(val authService: AuthService) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val _criticalError = MutableStateFlow<String?>(null)
    val isAuthenticated: StateFlow<Boolean> = authService.isAuthenticated
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, error = null) }
    fun onPasswordChange(password: String) = _uiState.update { it.copy(password = password, error = null) }
    fun onUsernameChange(username: String) = _uiState.update { it.copy(username = username, error = null) }
    fun onProfilePictureChange(bytes: ByteArray?) = _uiState.update { it.copy(profilePicture = bytes, error = null) }

    fun onFarmNameChange(farmName: String) = _uiState.update { it.copy(farmName = farmName) }
    fun onCountryChange(country: String) = _uiState.update { it.copy(country = country) }
    fun onRegionChange(region: String) = _uiState.update { it.copy(region = region) }
    fun onFarmSizeChange(size: String) = _uiState.update { it.copy(farmSize = size) }
    fun onFarmingTypeChange(type: String) = _uiState.update { it.copy(farmingType = type) }
    fun onIrrigationTypeChange(irrigation: String) = _uiState.update { it.copy(irrigationType = irrigation) }
    fun onSoilTypeChange(soil: String) = _uiState.update { it.copy(soilType = soil) }
    fun onExperienceLevelChange(level: String) = _uiState.update { it.copy(experienceLevel = level) }
    fun toggleCrop(crop: String) = _uiState.update { state ->
        val current = state.primaryCrops
        val updated = if (current.contains(crop)) current - crop else current + crop
        state.copy(primaryCrops = updated)
    }
    fun toggleCertification(cert: String) = _uiState.update { state ->
        val current = state.certifications
        val updated = if (current.contains(cert)) current - cert else current + cert
        state.copy(certifications = updated)
    }
    fun setSignUpStep(step: Int) = _uiState.update { it.copy(signUpStep = step, error = null) }

    fun onSignInClick() {
        if (_uiState.value.isLoading) return
        val (email, password) = _uiState.value
        if (!isEmailValid(email) || password.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a valid email and password.") }
            return
        }

        performAuthAction("Email Sign-In") {
            authService.signInWithEmail(email, password)
            authService.onSignInSuccess()
        }
    }

    fun onCreateAccountClick() {
        if (_uiState.value.isLoading) return
        val state = _uiState.value

        if (state.username.isBlank() || !isEmailValid(state.email) || state.password.length < 6) {
            _uiState.update { it.copy(error = "Please provide a username, valid email, and a password of at least 6 characters.") }
            return
        }

        performAuthAction("Account Creation") {
            val uid = authService.createAuthUser(state.email, state.password)
            authService.createUserProfile(
                uid = uid,
                email = state.email,
                username = state.username,
                profilePicture = state.profilePicture,
                farmName = state.farmName.ifBlank { null },
                country = state.country.ifBlank { null },
                region = state.region.ifBlank { null },
                farmSize = state.farmSize.ifBlank { null },
                primaryCrops = state.primaryCrops,
                farmingType = state.farmingType.ifBlank { null },
                irrigationType = state.irrigationType.ifBlank { null },
                soilType = state.soilType.ifBlank { null },
                experienceLevel = state.experienceLevel.ifBlank { null },
                certifications = state.certifications
            )

            authService.onSignInSuccess()
        }
    }


    private fun performAuthAction(actionName: String, action: suspend () -> Unit) {
        println("ViewModel: Starting action '$actionName'.")
        _uiState.update { it.copy(isLoading = true, error = null) }
        _criticalError.update { null }

        viewModelScope.launch {
            try {
                action()
                println("ViewModel: Action '$actionName' completed successfully.")
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred."
                println("ViewModel: Action '$actionName' FAILED. Error: $errorMessage")
                e.printStackTrace()

                if (errorMessage.contains("400") && (errorMessage.contains("Bad Request") || errorMessage.contains("INVALID_REFRESH_TOKEN"))) {
                    _criticalError.update { "Your session has expired. Please sign in again." }
                } else {
                    _uiState.update { it.copy(error = errorMessage) }
                }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotBlank() && "@" in email && email.substringAfterLast("@").contains(".")
    }
}
