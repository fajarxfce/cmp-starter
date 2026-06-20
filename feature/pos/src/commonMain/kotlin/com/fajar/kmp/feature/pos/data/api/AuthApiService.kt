package com.fajar.kmp.feature.pos.data.api

import com.fajar.kmp.core.network.ApiClient
import com.fajar.kmp.core.network.HttpMethod
import com.fajar.kmp.core.network.requestRaw
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

class AuthApiService(private val apiClient: ApiClient) {
    suspend fun login(request: AuthLoginRequest): AuthLoginResponse = requestBody(
        path = AuthApiPaths.login,
        body = PosApiJson.tolerant.encodeToString(request),
    )

    suspend fun register(request: AuthRegisterRequest): AuthRegisterResponse = requestBody(
        path = AuthApiPaths.register,
        body = PosApiJson.tolerant.encodeToString(request),
    )

    private suspend inline fun <reified T> requestBody(path: String, body: String): T =
        PosApiJson.tolerant.decodeFromString(apiClient.requestRaw(path = path, method = HttpMethod.Post, body = body))
}

private object AuthApiPaths {
    const val register = "/api/v1/auth/register"
    const val login = "/api/v1/auth/login"
}
