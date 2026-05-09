package com.fajar.kmp.core.network

class InMemoryApiClient(
    private val networkMonitor: NetworkMonitor,
    private val errorMapper: NetworkErrorMapper = NetworkErrorMapper(),
) : ApiClient {
    private val responses = mutableMapOf<String, ApiResponse>()

    fun register(path: String, response: ApiResponse) {
        responses[path] = response
    }

    override suspend fun execute(request: ApiRequest): NetworkResult<ApiResponse> {
        if (!networkMonitor.isOnline()) return NetworkResult.Failure(NetworkError.Offline)

        val response = responses[request.path] ?: return NetworkResult.Failure(NetworkError.Server(404, "Not found"))
        return if (response.statusCode in 200..299) {
            NetworkResult.Success(response)
        } else {
            NetworkResult.Failure(errorMapper.mapStatus(response.statusCode, response.body))
        }
    }
}

class StaticNetworkMonitor(private val online: Boolean) : NetworkMonitor {
    override suspend fun isOnline(): Boolean = online
}
