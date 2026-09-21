package org.vaulture.project.data.local

import com.russhwolf.settings.Settings

object OnboardingManager {
    private const val KEY_HAS_COMPLETED_ONBOARDING = "key_has_completed_onboarding"
    private var settings: Settings? = null

    var hasCompletedOnboarding: Boolean = false
        get() {
            val persisted = settings?.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)
            return if (persisted == true) true else field
        }
        private set

    fun init(settings: Settings) {
        this.settings = settings
        if (settings.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)) {
            hasCompletedOnboarding = true
        }
    }

    fun completeOnboarding() {
        hasCompletedOnboarding = true
        settings?.putBoolean(KEY_HAS_COMPLETED_ONBOARDING, true)
    }
}
