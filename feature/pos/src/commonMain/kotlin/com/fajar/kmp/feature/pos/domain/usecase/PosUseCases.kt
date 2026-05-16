package com.fajar.kmp.feature.pos.domain.usecase

import com.fajar.kmp.core.network.data.AuthLoginRequest
import com.fajar.kmp.core.network.data.AuthRegisterRequest
import com.fajar.kmp.core.network.data.CategoryCreateRequest
import com.fajar.kmp.core.network.data.ProductCreateRequest
import com.fajar.kmp.core.network.data.StoreRegisterRequest
import com.fajar.kmp.core.network.data.SyncRequest
import com.fajar.kmp.core.network.data.TransactionCreateRequest
import com.fajar.kmp.feature.pos.domain.repository.PosRepository

class PosLoginUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(request: AuthLoginRequest) = repository.login(request)
}

class PosRegisterUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(request: AuthRegisterRequest) = repository.register(request)
}

class RegisterStoreUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(request: StoreRegisterRequest) = repository.registerStore(request)
}

class ListCategoriesUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(storeId: String) = repository.listCategories(storeId)
}

class CreateCategoryUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(storeId: String, request: CategoryCreateRequest) = repository.createCategory(storeId, request)
}

class ListProductsUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(storeId: String) = repository.listProducts(storeId)
}

class CreateProductUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(storeId: String, request: ProductCreateRequest) = repository.createProduct(storeId, request)
}

class ListTransactionsUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(storeId: String) = repository.listTransactions(storeId)
}

class CreateTransactionUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(storeId: String, request: TransactionCreateRequest) = repository.createTransaction(storeId, request)
}

class SyncStoreUseCase(private val repository: PosRepository) {
    suspend operator fun invoke(storeId: String, request: SyncRequest) = repository.sync(storeId, request)
}

class AdminStatsUseCase(private val repository: PosRepository) {
    suspend operator fun invoke() = repository.adminStats()
}

class AdminStoresUseCase(private val repository: PosRepository) {
    suspend operator fun invoke() = repository.adminStores()
}

class AdminUsersUseCase(private val repository: PosRepository) {
    suspend operator fun invoke() = repository.adminUsers()
}
