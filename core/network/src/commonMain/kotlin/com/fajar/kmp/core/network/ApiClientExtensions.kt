package com.fajar.kmp.core.network

suspend fun ApiClient.executeOrThrow(request: ApiRequest): ApiResponse = when (val result = execute(request)) {
    is NetworkResult.Failure -> error(result.error.toString())
    is NetworkResult.Success -> result.value
}

suspend fun ApiClient.requestRaw(
    path: String,
    method: HttpMethod,
    body: String? = null,
    headers: Map<String, String> = emptyMap(),
    idempotencyKey: String? = null,
): String = executeOrThrow(
    ApiRequest(
        path = path,
        method = method,
        headers = headers,
        body = body,
        idempotencyKey = idempotencyKey,
    ),
).body
