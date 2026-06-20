package com.fajar.kmp.feature.pos.data

import com.fajar.kmp.core.common.result.Try
import com.fajar.kmp.core.common.result.runOperationCatching
import com.fajar.kmp.core.datastore.SessionPreferences
import com.fajar.kmp.feature.pos.data.api.AuthApiService
import com.fajar.kmp.feature.pos.data.api.AuthLoginRequest
import com.fajar.kmp.feature.pos.data.api.AuthRegisterRequest
import com.fajar.kmp.feature.pos.data.api.CategoryCreateRequest
import com.fajar.kmp.feature.pos.data.api.PosApiService
import com.fajar.kmp.feature.pos.data.api.ProductCreateRequest
import com.fajar.kmp.feature.pos.data.api.StoreRegisterRequest
import com.fajar.kmp.feature.pos.data.api.SyncRequest
import com.fajar.kmp.feature.pos.data.api.TransactionCreateRequest
import com.fajar.kmp.feature.pos.domain.repository.PosRepository

class PosRepositoryImpl(
    private val authApiService: AuthApiService,
    private val posApiService: PosApiService,
    private val sessionPreferences: SessionPreferences,
) : PosRepository {
    override suspend fun login(request: AuthLoginRequest): Try<String, Throwable> = runOperationCatching {
        val response = authApiService.login(request)
        val token = response.data?.accessToken ?: error("accessToken missing from login response")
        sessionPreferences.saveAccessToken(token)
        val stores = posApiService.listStores()
        val store = stores.firstOrNull { it.isActive } ?: stores.firstOrNull()
        store?.id?.let { sessionPreferences.saveActiveStoreId(it) }
        token
    }

    override suspend fun register(request: AuthRegisterRequest): Try<String?, Throwable> = runOperationCatching {
        val response = authApiService.register(request)
        val token = response.data?.accessToken
        token?.let { sessionPreferences.saveAccessToken(it) }
        val stores = posApiService.listStores()
        val store = stores.firstOrNull { it.isActive } ?: stores.firstOrNull()
        store?.id?.let { sessionPreferences.saveActiveStoreId(it) }
        token
    }

    override suspend fun registerStore(request: StoreRegisterRequest): Try<String, Throwable> = runOperationCatching {
        val storeId = posApiService.registerStore(request)
        sessionPreferences.saveActiveStoreId(storeId)
        storeId
    }

    override suspend fun listCategories(storeId: String): Try<String, Throwable> = runOperationCatching { posApiService.listCategories(storeId) }
    override suspend fun createCategory(storeId: String, request: CategoryCreateRequest): Try<String, Throwable> = runOperationCatching { posApiService.createCategory(storeId, request) }
    override suspend fun listProducts(storeId: String): Try<String, Throwable> = runOperationCatching { posApiService.listProducts(storeId) }
    override suspend fun createProduct(storeId: String, request: ProductCreateRequest): Try<String, Throwable> = runOperationCatching { posApiService.createProduct(storeId, request) }
    override suspend fun listTransactions(storeId: String): Try<String, Throwable> = runOperationCatching { posApiService.listTransactions(storeId) }
    override suspend fun createTransaction(storeId: String, request: TransactionCreateRequest): Try<String, Throwable> = runOperationCatching { posApiService.createTransaction(storeId, request) }
    override suspend fun sync(storeId: String, request: SyncRequest): Try<String, Throwable> = runOperationCatching { posApiService.sync(storeId, request) }
    override suspend fun adminStats(): Try<String, Throwable> = runOperationCatching { posApiService.adminStats() }
    override suspend fun adminStores(): Try<String, Throwable> = runOperationCatching { posApiService.adminStores() }
    override suspend fun adminUsers(): Try<String, Throwable> = runOperationCatching { posApiService.adminUsers() }
}
