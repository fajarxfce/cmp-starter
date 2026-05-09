package com.fajar.kmp.feature.auth.data

import com.fajar.kmp.core.datastore.InMemoryKeyValueStore
import com.fajar.kmp.core.datastore.SecureKeyValueStore
import com.fajar.kmp.feature.auth.domain.repository.AuthRepository
import com.fajar.kmp.feature.auth.domain.usecase.LoginUseCase
import com.fajar.kmp.feature.auth.domain.usecase.LogoutUseCase
import com.fajar.kmp.feature.auth.domain.usecase.ObserveAuthSessionUseCase
import com.fajar.kmp.feature.auth.domain.usecase.RefreshTokenUseCase
import com.fajar.kmp.feature.auth.presentation.viewmodel.AuthViewModel

class AuthGraph {
    private val repository: AuthRepository = AuthRepositoryImpl(SecureKeyValueStore(InMemoryKeyValueStore()))

    fun viewModel(): AuthViewModel = AuthViewModel(
        observeSession = ObserveAuthSessionUseCase(repository),
        login = LoginUseCase(repository),
        logout = LogoutUseCase(repository),
        refreshToken = RefreshTokenUseCase(repository),
    )
}
