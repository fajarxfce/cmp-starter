package com.fajar.kmp.feature.auth.presentation.viewmodel

import com.fajar.kmp.feature.auth.domain.model.AuthResult
import com.fajar.kmp.feature.auth.domain.usecase.LoginUseCase
import com.fajar.kmp.feature.auth.domain.usecase.LogoutUseCase
import com.fajar.kmp.feature.auth.domain.usecase.ObserveAuthSessionUseCase
import com.fajar.kmp.feature.auth.domain.usecase.RefreshTokenUseCase
import com.fajar.kmp.feature.auth.presentation.contract.AuthEffect
import com.fajar.kmp.feature.auth.presentation.contract.AuthIntent
import com.fajar.kmp.feature.auth.presentation.contract.AuthPartialChange
import com.fajar.kmp.feature.auth.presentation.contract.AuthReducer
import com.fajar.kmp.feature.auth.presentation.contract.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val observeSession: ObserveAuthSessionUseCase,
    private val login: LoginUseCase,
    private val logout: LogoutUseCase,
    private val refreshToken: RefreshTokenUseCase,
    private val reducer: AuthReducer = AuthReducer(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val mutableState = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = mutableState.asStateFlow()

    private val mutableEffect = MutableSharedFlow<AuthEffect>()
    val effect: SharedFlow<AuthEffect> = mutableEffect.asSharedFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            AuthIntent.ObserveSession -> observe()
            is AuthIntent.Login -> doLogin(intent.email, intent.password)
            AuthIntent.Logout -> doLogout()
            AuthIntent.RefreshToken -> doRefresh()
        }
    }

    private fun observe() {
        scope.launch {
            observeSession().collect { applyChange(AuthPartialChange.SessionChanged(it)) }
        }
    }

    private fun doLogin(email: String, password: String) {
        applyChange(AuthPartialChange.Loading)
        scope.launch {
            when (val result = login(email, password)) {
                is AuthResult.Success -> {
                    applyChange(AuthPartialChange.SessionChanged(result.value))
                    mutableEffect.emit(AuthEffect.NavigateToHome)
                }
                is AuthResult.Failure -> applyChange(AuthPartialChange.Error(result.error))
            }
        }
    }

    private fun doLogout() {
        scope.launch {
            logout()
            mutableEffect.emit(AuthEffect.NavigateToLogin)
        }
    }

    private fun doRefresh() {
        scope.launch {
            when (val result = refreshToken()) {
                is AuthResult.Success -> applyChange(AuthPartialChange.SessionChanged(result.value))
                is AuthResult.Failure -> applyChange(AuthPartialChange.Error(result.error))
            }
        }
    }

    private fun applyChange(change: AuthPartialChange) {
        mutableState.value = reducer.reduce(mutableState.value, change)
    }
}
