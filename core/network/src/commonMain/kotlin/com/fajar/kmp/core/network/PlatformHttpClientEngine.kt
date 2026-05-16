package com.fajar.kmp.core.network

import io.ktor.client.engine.HttpClientEngineFactory

internal expect fun platformHttpClientEngineFactory(): HttpClientEngineFactory<*>
