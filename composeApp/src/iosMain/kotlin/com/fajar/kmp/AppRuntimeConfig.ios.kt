package com.fajar.kmp

import com.fajar.kmp.core.common.AppFlavor
import com.fajar.kmp.core.common.ProxyConfig
import com.fajar.kmp.core.common.StaticAppConfig

actual object AppRuntimeConfig {
    actual val config: StaticAppConfig = StaticAppConfig(
        baseUrl = "REPLACE_WITH_IOS_BASE_URL",
        appName = "CMP Starter iOS",
        isDebugLoggingEnabled = true,
        isMockModeEnabled = true,
        isAnalyticsEnabled = false,
        flavor = AppFlavor.Dev,
        proxy = ProxyConfig(),
    )
}
