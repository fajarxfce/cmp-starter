package com.fajar.kmp.feature.pos.domain.repository

import com.fajar.kmp.core.network.data.AuthLoginRequest
import com.fajar.kmp.core.network.data.AuthRegisterRequest
import com.fajar.kmp.core.network.data.CategoryCreateRequest
import com.fajar.kmp.core.network.data.ProductCreateRequest
import com.fajar.kmp.core.network.data.StoreRegisterRequest
import com.fajar.kmp.core.network.data.SyncRequest
import com.fajar.kmp.core.network.data.TransactionCreateRequest

interface PosRepository {
    suspend fun login(request: AuthLoginRequest): PosResult<String>
    suspend fun register(request: AuthRegisterRequest): PosResult<String?>
    suspend fun registerStore(request: StoreRegisterRequest): PosResult<String>
    suspend fun listCategories(storeId: String): PosResult<String>
    suspend fun createCategory(storeId: String, request: CategoryCreateRequest): PosResult<String>
    suspend fun listProducts(storeId: String): PosResult<String>
    suspend fun createProduct(storeId: String, request: ProductCreateRequest): PosResult<String>
    suspend fun listTransactions(storeId: String): PosResult<String>
    suspend fun createTransaction(storeId: String, request: TransactionCreateRequest): PosResult<String>
    suspend fun sync(storeId: String, request: SyncRequest): PosResult<String>
    suspend fun adminStats(): PosResult<String>
    suspend fun adminStores(): PosResult<String>
    suspend fun adminUsers(): PosResult<String>
}

sealed interface PosResult<out T> {
    data class Success<T>(val value: T) : PosResult<T>
    data class Failure(val error: PosError) : PosResult<Nothing>
}

sealed interface PosError {
    data object Unauthorized : PosError
    data class MissingData(val message: String) : PosError
    data class Network(val message: String) : PosError
}
