package com.fajar.kmp

import com.fajar.kmp.core.common.StaticAppConfig

expect object AppRuntimeConfig {
    val config: StaticAppConfig
}
