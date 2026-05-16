package com.fajar.kmp.feature.pos.data

import com.fajar.kmp.core.datastore.SessionPreferences
import com.fajar.kmp.core.network.ApiClient
import com.fajar.kmp.core.network.ApiRequest
import com.fajar.kmp.core.network.ApiResponse
import com.fajar.kmp.core.network.HttpMethod
import com.fajar.kmp.core.network.NetworkError
import com.fajar.kmp.core.network.NetworkResult
import com.fajar.kmp.core.network.data.AuthLoginRequest
import com.fajar.kmp.core.network.data.AuthLoginResponse
import com.fajar.kmp.core.network.data.AuthRegisterRequest
import com.fajar.kmp.core.network.data.AuthRegisterResponse
import com.fajar.kmp.core.network.data.CategoryCreateRequest
import com.fajar.kmp.core.network.data.PosApiJson
import com.fajar.kmp.core.network.data.PosApiPaths
import com.fajar.kmp.core.network.data.ProductCreateRequest
import com.fajar.kmp.core.network.data.StoreRegisterRequest
import com.fajar.kmp.core.network.data.SyncRequest
import com.fajar.kmp.core.network.data.TransactionCreateRequest
import com.fajar.kmp.feature.pos.domain.repository.PosError
import com.fajar.kmp.feature.pos.domain.repository.PosRepository
import com.fajar.kmp.feature.pos.domain.repository.PosResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class PosRepositoryImpl(
    private val apiClient: ApiClient,
    private val sessionPreferences: SessionPreferences,
) : PosRepository {
    override suspend fun login(request: AuthLoginRequest): PosResult<String> {
        val response = when (val result = execute(PosApiPaths.authLogin, HttpMethod.Post, PosApiJson.tolerant.encodeToString(request))) {
            is PosResult.Success -> result.value
            is PosResult.Failure -> return result
        }
        val envelope = PosApiJson.tolerant.decodeFromString<AuthLoginResponse>(response.body)
        val token = envelope.data?.accessToken
            ?: return PosResult.Failure(PosError.MissingData("accessToken missing from login response"))
        sessionPreferences.saveAccessToken(token)
        return PosResult.Success(token)
    }

    override suspend fun register(request: AuthRegisterRequest): PosResult<String?> {
        val response = when (val result = execute(PosApiPaths.authRegister, HttpMethod.Post, PosApiJson.tolerant.encodeToString(request))) {
            is PosResult.Success -> result.value
            is PosResult.Failure -> return result
        }
        val envelope = PosApiJson.tolerant.decodeFromString<AuthRegisterResponse>(response.body)
        return PosResult.Success(envelope.data?.accessToken)
    }

    override suspend fun registerStore(request: StoreRegisterRequest): PosResult<String> {
        val response = when (val result = execute(PosApiPaths.storeRegister, HttpMethod.Post, PosApiJson.tolerant.encodeToString(request))) {
            is PosResult.Success -> result.value
            is PosResult.Failure -> return result
        }
        val storeId = extractStoreId(response.body)
            ?: return PosResult.Failure(PosError.MissingData("storeId missing from store register response"))
        sessionPreferences.saveActiveStoreId(storeId)
        return PosResult.Success(storeId)
    }

    override suspend fun listCategories(storeId: String): PosResult<String> =
        requestRaw(PosApiPaths.storeCategories(storeId), HttpMethod.Get)

    override suspend fun createCategory(storeId: String, request: CategoryCreateRequest): PosResult<String> =
        requestRaw(PosApiPaths.storeCategories(storeId), HttpMethod.Post, PosApiJson.tolerant.encodeToString(request))

    override suspend fun listProducts(storeId: String): PosResult<String> =
        requestRaw(PosApiPaths.storeProducts(storeId), HttpMethod.Get)

    override suspend fun createProduct(storeId: String, request: ProductCreateRequest): PosResult<String> =
        requestRaw(PosApiPaths.storeProducts(storeId), HttpMethod.Post, PosApiJson.tolerant.encodeToString(request))

    override suspend fun listTransactions(storeId: String): PosResult<String> =
        requestRaw(PosApiPaths.storeTransactions(storeId), HttpMethod.Get)

    override suspend fun createTransaction(storeId: String, request: TransactionCreateRequest): PosResult<String> =
        requestRaw(PosApiPaths.storeTransactions(storeId), HttpMethod.Post, PosApiJson.tolerant.encodeToString(request))

    override suspend fun sync(storeId: String, request: SyncRequest): PosResult<String> =
        requestRaw(PosApiPaths.storeSync(storeId), HttpMethod.Post, PosApiJson.tolerant.encodeToString(request))

    override suspend fun adminStats(): PosResult<String> = requestRaw(PosApiPaths.adminStats, HttpMethod.Get)

    override suspend fun adminStores(): PosResult<String> = requestRaw(PosApiPaths.adminStores, HttpMethod.Get)

    override suspend fun adminUsers(): PosResult<String> = requestRaw(PosApiPaths.adminUsers, HttpMethod.Get)

    private suspend fun requestRaw(path: String, method: HttpMethod, body: String? = null): PosResult<String> {
        return when (val result = execute(path, method, body)) {
            is PosResult.Success -> PosResult.Success(result.value.body)
            is PosResult.Failure -> result
        }
    }

    private suspend fun execute(path: String, method: HttpMethod, body: String? = null): PosResult<ApiResponse> {
        return when (val result = apiClient.execute(ApiRequest(path = path, method = method, body = body))) {
            is NetworkResult.Success -> PosResult.Success(result.value)
            is NetworkResult.Failure -> PosResult.Failure(result.error.toPosError())
        }
    }

    private fun extractStoreId(body: String): String? {
        val root = PosApiJson.tolerant.parseToJsonElement(body) as? JsonObject ?: return null
        val data = root["data"] as? JsonObject ?: return null
        return data.stringValue("storeId") ?: data.stringValue("id")
    }

    private fun JsonObject.stringValue(key: String): String? = this[key]?.jsonPrimitive?.content

    private fun NetworkError.toPosError(): PosError = when (this) {
        NetworkError.Unauthorized -> PosError.Unauthorized
        NetworkError.Offline -> PosError.Network("offline")
        is NetworkError.Server -> PosError.Network(message)
        is NetworkError.Unknown -> PosError.Network(message)
    }
}
