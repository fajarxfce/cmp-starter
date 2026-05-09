package com.fajar.kmp.core.common

interface AppConfig {
    val baseUrl: String
    val appName: String
    val isDebugLoggingEnabled: Boolean
    val isMockModeEnabled: Boolean
    val isAnalyticsEnabled: Boolean
    val flavor: AppFlavor
}

enum class AppFlavor {
    Dev,
    Staging,
    Prod,
}

data class StaticAppConfig(
    override val baseUrl: String,
    override val appName: String,
    override val isDebugLoggingEnabled: Boolean,
    override val isMockModeEnabled: Boolean,
    override val isAnalyticsEnabled: Boolean,
    override val flavor: AppFlavor,
) : AppConfig
