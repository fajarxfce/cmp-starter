package com.fajar.kmp.feature.auth.domain.usecase

import com.fajar.kmp.feature.auth.domain.repository.AuthRepository

class ObserveAuthSessionUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.observeSession()
}

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email.trim(), password)
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}

class RefreshTokenUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.refreshToken()
}
