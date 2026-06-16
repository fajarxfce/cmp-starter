package com.fajar.kmp.core.common

interface AppConfig {
    val baseUrl: String
    val appName: String
    val isDebugLoggingEnabled: Boolean
    val isMockModeEnabled: Boolean
    val isAnalyticsEnabled: Boolean
    val flavor: AppFlavor
    val proxy: ProxyConfig
}

data class ProxyConfig(
    val enabled: Boolean = false,
    val host: String = "",
    val port: Int = 0,
) {
    val url: String?
        get() = if (enabled && host.isNotBlank() && port > 0) "http://$host:$port" else null
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
    override val proxy: ProxyConfig = ProxyConfig(),
) : AppConfig
