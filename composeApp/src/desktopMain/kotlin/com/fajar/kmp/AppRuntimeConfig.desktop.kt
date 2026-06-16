package com.fajar.kmp

import com.fajar.kmp.core.common.AppFlavor
import com.fajar.kmp.core.common.ProxyConfig
import com.fajar.kmp.core.common.StaticAppConfig

actual object AppRuntimeConfig {
    actual val config: StaticAppConfig = StaticAppConfig(
        baseUrl = "http://10.60.40.185:8080",
        appName = "Kasir POS Desktop",
        isDebugLoggingEnabled = true,
        isMockModeEnabled = false,
        isAnalyticsEnabled = false,
        flavor = AppFlavor.Dev,
        proxy = ProxyConfig(),
    )
}
