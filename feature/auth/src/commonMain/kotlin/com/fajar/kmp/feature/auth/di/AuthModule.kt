package com.fajar.kmp.feature.auth.di

import com.fajar.kmp.feature.auth.data.AuthRepositoryImpl
import com.fajar.kmp.feature.auth.domain.repository.AuthRepository
import com.fajar.kmp.feature.auth.domain.usecase.LoginUseCase
import com.fajar.kmp.feature.auth.domain.usecase.LogoutUseCase
import com.fajar.kmp.feature.auth.domain.usecase.ObserveAuthSessionUseCase
import com.fajar.kmp.feature.auth.domain.usecase.RefreshTokenUseCase
import com.fajar.kmp.feature.auth.presentation.viewmodel.AuthViewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { ObserveAuthSessionUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { RefreshTokenUseCase(get()) }
    factory {
        AuthViewModel(
            observeSession = get(),
            login = get(),
            logout = get(),
            refreshToken = get(),
        )
    }
}
