package com.fajar.kmp.core.network

import com.fajar.kmp.core.common.AppConfig
import org.koin.dsl.module

val networkModule = module {
    single { NetworkErrorMapper() }
    single<NetworkMonitor> { StaticNetworkMonitor(online = true) }
    single<BearerTokenProvider> { BearerTokenProvider { null } }
    single<ApiClient> {
        val config = get<AppConfig>()
        KtorApiClient(
            baseUrl = config.baseUrl,
            networkMonitor = get(),
            bearerTokenProvider = get(),
            errorMapper = get(),
            proxyUrl = config.proxy.url,
        )
    }
}
