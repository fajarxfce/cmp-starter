package com.fajar.kmp

import com.fajar.kmp.core.common.AppFlavor
import com.fajar.kmp.core.common.ProxyConfig
import com.fajar.kmp.core.common.StaticAppConfig

actual object AppRuntimeConfig {
    actual val config: StaticAppConfig = StaticAppConfig(
        baseUrl = BuildConfig.BASE_URL,
        appName = "CMP Starter",
        isDebugLoggingEnabled = BuildConfig.DEBUG,
        isMockModeEnabled = BuildConfig.MOCK_MODE,
        isAnalyticsEnabled = !BuildConfig.DEBUG,
        flavor = when (BuildConfig.FLAVOR_NAME) {
            "staging" -> AppFlavor.Staging
            "prod", "prodProxy" -> AppFlavor.Prod
            else -> AppFlavor.Dev
        },
        proxy = ProxyConfig(
            enabled = BuildConfig.PROXY_ENABLED,
            host = BuildConfig.PROXY_HOST,
            port = BuildConfig.PROXY_PORT.toInt(),
        ),
    )
}
