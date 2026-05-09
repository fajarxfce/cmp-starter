package com.fajar.kmp.feature.settings

data class SettingsState(
    val darkModeEnabled: Boolean = false,
    val analyticsEnabled: Boolean = false,
)

class SettingsReducer {
    fun toggleDarkMode(state: SettingsState): SettingsState = state.copy(darkModeEnabled = !state.darkModeEnabled)
    fun toggleAnalytics(state: SettingsState): SettingsState = state.copy(analyticsEnabled = !state.analyticsEnabled)
}
