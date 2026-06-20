package com.fajar.kmp.feature.pos.domain.repository

import com.fajar.kmp.core.common.result.Try
import com.fajar.kmp.feature.pos.data.api.AuthLoginRequest
import com.fajar.kmp.feature.pos.data.api.AuthRegisterRequest
import com.fajar.kmp.feature.pos.data.api.CategoryCreateRequest
import com.fajar.kmp.feature.pos.data.api.ProductCreateRequest
import com.fajar.kmp.feature.pos.data.api.StoreRegisterRequest
import com.fajar.kmp.feature.pos.data.api.SyncRequest
import com.fajar.kmp.feature.pos.data.api.TransactionCreateRequest

interface PosRepository {
    suspend fun login(request: AuthLoginRequest): Try<String, Throwable>
    suspend fun register(request: AuthRegisterRequest): Try<String?, Throwable>
    suspend fun registerStore(request: StoreRegisterRequest): Try<String, Throwable>
    suspend fun listCategories(storeId: String): Try<String, Throwable>
    suspend fun createCategory(storeId: String, request: CategoryCreateRequest): Try<String, Throwable>
    suspend fun listProducts(storeId: String): Try<String, Throwable>
    suspend fun createProduct(storeId: String, request: ProductCreateRequest): Try<String, Throwable>
    suspend fun listTransactions(storeId: String): Try<String, Throwable>
    suspend fun createTransaction(storeId: String, request: TransactionCreateRequest): Try<String, Throwable>
    suspend fun sync(storeId: String, request: SyncRequest): Try<String, Throwable>
    suspend fun adminStats(): Try<String, Throwable>
    suspend fun adminStores(): Try<String, Throwable>
    suspend fun adminUsers(): Try<String, Throwable>
}
