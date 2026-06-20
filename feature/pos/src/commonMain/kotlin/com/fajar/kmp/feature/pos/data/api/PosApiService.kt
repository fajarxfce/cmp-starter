package com.fajar.kmp.feature.pos.data.api

import com.fajar.kmp.core.network.ApiRequest
import com.fajar.kmp.core.network.ApiClient
import com.fajar.kmp.core.network.ApiResponseEnvelope
import com.fajar.kmp.core.network.HttpMethod
import com.fajar.kmp.core.network.executeOrThrow
import com.fajar.kmp.core.network.requestRaw
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

class PosApiService(private val apiClient: ApiClient) {
    suspend fun registerStore(request: StoreRegisterRequest): String {
        val response = apiClient.executeOrThrow(
            ApiRequest(
                path = PosFeatureApiPaths.storeRegister,
                method = HttpMethod.Post,
                body = PosApiJson.tolerant.encodeToString(request),
            ),
        )
        val envelope = PosApiJson.tolerant.decodeFromString<ApiResponseEnvelope<StoreSummaryResponse>>(response.body)
        return envelope.data?.id ?: error(envelope.error?.message ?: "Store id is empty")
    }

    suspend fun listStores(): List<StoreSummaryResponse> {
        val response = apiClient.executeOrThrow(
            ApiRequest(path = PosFeatureApiPaths.adminStores, method = HttpMethod.Get),
        )
        val envelope = PosApiJson.tolerant.decodeFromString<ApiResponseEnvelope<List<StoreSummaryResponse>>>(response.body)
        return envelope.data ?: emptyList()
    }

    suspend fun listCategories(storeId: String): String =
        apiClient.requestRaw(PosFeatureApiPaths.storeCategories(storeId), HttpMethod.Get)

    suspend fun createCategory(storeId: String, request: CategoryCreateRequest): String = apiClient.requestRaw(
        path = PosFeatureApiPaths.storeCategories(storeId),
        method = HttpMethod.Post,
        body = PosApiJson.tolerant.encodeToString(request),
    )

    suspend fun listProducts(storeId: String): String =
        apiClient.requestRaw(PosFeatureApiPaths.storeProducts(storeId), HttpMethod.Get)

    suspend fun createProduct(storeId: String, request: ProductCreateRequest): String = apiClient.requestRaw(
        path = PosFeatureApiPaths.storeProducts(storeId),
        method = HttpMethod.Post,
        body = PosApiJson.tolerant.encodeToString(request),
    )

    suspend fun listTransactions(storeId: String): String =
        apiClient.requestRaw(PosFeatureApiPaths.storeTransactions(storeId), HttpMethod.Get)

    suspend fun createTransaction(storeId: String, request: TransactionCreateRequest): String = apiClient.requestRaw(
        path = PosFeatureApiPaths.storeTransactions(storeId),
        method = HttpMethod.Post,
        body = PosApiJson.tolerant.encodeToString(request),
    )

    suspend fun sync(storeId: String, request: SyncRequest): String = apiClient.requestRaw(
        path = PosFeatureApiPaths.storeSync(storeId),
        method = HttpMethod.Post,
        body = PosApiJson.tolerant.encodeToString(request),
    )

    suspend fun adminStats(): String = apiClient.requestRaw(PosFeatureApiPaths.adminStats, HttpMethod.Get)
    suspend fun adminStores(): String = apiClient.requestRaw(PosFeatureApiPaths.adminStores, HttpMethod.Get)
    suspend fun adminUsers(): String = apiClient.requestRaw(PosFeatureApiPaths.adminUsers, HttpMethod.Get)
}

private object PosFeatureApiPaths {
    const val storeRegister = "/api/v1/stores/register"
    const val adminStats = "/api/v1/admin/stats"
    const val adminStores = "/api/v1/admin/stores"
    const val adminUsers = "/api/v1/admin/users"
    fun storeCategories(storeId: String): String = "/api/v1/stores/$storeId/categories"
    fun storeProducts(storeId: String): String = "/api/v1/stores/$storeId/products"
    fun storeTransactions(storeId: String): String = "/api/v1/stores/$storeId/transactions"
    fun storeSync(storeId: String): String = "/api/v1/stores/$storeId/sync"
}
