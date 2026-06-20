package com.fajar.kmp.core.network

import kotlinx.serialization.json.Json

object NetworkJson {
    val tolerant: Json = Json {
        ignoreUnknownKeys = true
    }
}
