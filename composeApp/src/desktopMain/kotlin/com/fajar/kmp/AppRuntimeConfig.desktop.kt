package com.fajar.kmp

import com.fajar.kmp.core.common.AppFlavor
import com.fajar.kmp.core.common.StaticAppConfig

actual object AppRuntimeConfig {
    actual val config: StaticAppConfig = StaticAppConfig(
        baseUrl = "REPLACE_WITH_DESKTOP_BASE_URL",
        appName = "CMP Starter Desktop",
        isDebugLoggingEnabled = true,
        isMockModeEnabled = true,
        isAnalyticsEnabled = false,
        flavor = AppFlavor.Dev,
    )
}
