package com.fajar.kmp.core.network

import com.fajar.kmp.core.common.AppConfig
import com.fajar.kmp.core.datastore.SessionPreferences
import org.koin.dsl.module

val networkModule = module {
    single { NetworkErrorMapper() }
    single<NetworkMonitor> { StaticNetworkMonitor(online = true) }
    single<BearerTokenProvider> { BearerTokenProvider { get<SessionPreferences>().getAccessToken() } }
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
