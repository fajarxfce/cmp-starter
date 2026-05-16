package com.fajar.kmp.core.network

import com.fajar.kmp.core.common.AppConfig
import org.koin.dsl.module

val networkModule = module {
    single { NetworkErrorMapper() }
    single<NetworkMonitor> { StaticNetworkMonitor(online = true) }
    single<BearerTokenProvider> { BearerTokenProvider { null } }
    single<ApiClient> {
        KtorApiClient(
            baseUrl = get<AppConfig>().baseUrl,
            networkMonitor = get(),
            bearerTokenProvider = get(),
            errorMapper = get(),
        )
    }
}
