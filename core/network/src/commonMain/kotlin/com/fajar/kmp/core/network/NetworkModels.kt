package com.fajar.kmp.core.network

sealed interface NetworkError {
    data object Offline : NetworkError
    data object Unauthorized : NetworkError
    data class Server(val code: Int, val message: String) : NetworkError
    data class Unknown(val message: String) : NetworkError
}

interface NetworkMonitor {
    suspend fun isOnline(): Boolean
}

data class ApiRequest(
    val path: String,
    val method: HttpMethod,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val idempotencyKey: String? = null,
)

data class ApiResponse(
    val statusCode: Int,
    val body: String,
    val headers: Map<String, String> = emptyMap(),
)

enum class HttpMethod { Get, Post, Put, Patch, Delete }

interface ApiClient {
    suspend fun execute(request: ApiRequest): NetworkResult<ApiResponse>
}

sealed interface NetworkResult<out T> {
    data class Success<T>(val value: T) : NetworkResult<T>
    data class Failure(val error: NetworkError) : NetworkResult<Nothing>
}

class NetworkErrorMapper {
    fun mapStatus(code: Int, message: String): NetworkError = when (code) {
        401 -> NetworkError.Unauthorized
        in 500..599 -> NetworkError.Server(code, message)
        else -> NetworkError.Unknown(message)
    }
}

class RequestIdProvider {
    private var nextId = 0L

    fun next(): String {
        nextId += 1
        return "request-$nextId"
    }
}
