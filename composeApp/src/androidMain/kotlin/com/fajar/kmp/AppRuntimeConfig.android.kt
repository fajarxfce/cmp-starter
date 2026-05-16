package com.fajar.kmp

import com.fajar.kmp.core.common.AppFlavor
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
            "prod" -> AppFlavor.Prod
            else -> AppFlavor.Dev
        },
    )
}
