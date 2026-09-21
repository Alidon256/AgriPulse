package org.vaulture.project.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.russhwolf.settings.Settings
import org.vaulture.project.features.profile.presentation.viewmodel.SettingsViewModel

@Composable
actual fun getSettingsViewModel(): SettingsViewModel {
    return remember {
        val settingsFactory = SettingsFactory()
        val settings: Settings = settingsFactory.createSettings()
        SettingsViewModel(settings)
    }
}
