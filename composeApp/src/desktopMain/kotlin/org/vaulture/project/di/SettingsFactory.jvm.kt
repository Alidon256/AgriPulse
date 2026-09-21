package org.vaulture.project.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual class SettingsFactory {
    actual fun createSettings(): Settings {
        val preferences = Preferences.userRoot().node("AgriPulse")
        return PreferencesSettings(preferences)
    }
}
