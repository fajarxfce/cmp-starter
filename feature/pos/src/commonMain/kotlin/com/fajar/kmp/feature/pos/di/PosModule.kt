package com.fajar.kmp.feature.pos.di

import com.fajar.kmp.core.network.ApiClient
import com.fajar.kmp.feature.pos.data.PosRepositoryImpl
import com.fajar.kmp.feature.pos.data.api.AuthApiService
import com.fajar.kmp.feature.pos.data.api.PosApiService
import com.fajar.kmp.feature.pos.domain.repository.PosRepository
import com.fajar.kmp.feature.pos.domain.usecase.AdminStatsUseCase
import com.fajar.kmp.feature.pos.domain.usecase.AdminStoresUseCase
import com.fajar.kmp.feature.pos.domain.usecase.AdminUsersUseCase
import com.fajar.kmp.feature.pos.domain.usecase.CreateCategoryUseCase
import com.fajar.kmp.feature.pos.domain.usecase.CreateProductUseCase
import com.fajar.kmp.feature.pos.domain.usecase.CreateTransactionUseCase
import com.fajar.kmp.feature.pos.domain.usecase.ListCategoriesUseCase
import com.fajar.kmp.feature.pos.domain.usecase.ListProductsUseCase
import com.fajar.kmp.feature.pos.domain.usecase.ListTransactionsUseCase
import com.fajar.kmp.feature.pos.domain.usecase.PosLoginUseCase
import com.fajar.kmp.feature.pos.domain.usecase.PosRegisterUseCase
import com.fajar.kmp.feature.pos.domain.usecase.RegisterStoreUseCase
import com.fajar.kmp.feature.pos.domain.usecase.SyncStoreUseCase
import org.koin.dsl.module

val posModule = module {
    single { AuthApiService(get<ApiClient>()) }
    single { PosApiService(get<ApiClient>()) }
    single<PosRepository> { PosRepositoryImpl(get(), get(), get()) }
    factory { PosLoginUseCase(get()) }
    factory { PosRegisterUseCase(get()) }
    factory { RegisterStoreUseCase(get()) }
    factory { ListCategoriesUseCase(get()) }
    factory { CreateCategoryUseCase(get()) }
    factory { ListProductsUseCase(get()) }
    factory { CreateProductUseCase(get()) }
    factory { ListTransactionsUseCase(get()) }
    factory { CreateTransactionUseCase(get()) }
    factory { SyncStoreUseCase(get()) }
    factory { AdminStatsUseCase(get()) }
    factory { AdminStoresUseCase(get()) }
    factory { AdminUsersUseCase(get()) }
}
