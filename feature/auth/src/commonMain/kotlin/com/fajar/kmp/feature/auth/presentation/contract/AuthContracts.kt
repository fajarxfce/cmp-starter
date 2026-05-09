package com.fajar.kmp.feature.auth.presentation.contract

import com.fajar.kmp.feature.auth.domain.model.AuthError
import com.fajar.kmp.feature.auth.domain.model.AuthSession

sealed interface AuthIntent {
    data object ObserveSession : AuthIntent
    data class Login(val email: String, val password: String) : AuthIntent
    data object Logout : AuthIntent
    data object RefreshToken : AuthIntent
}

data class AuthState(
    val isLoading: Boolean = false,
    val email: String? = null,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface AuthEffect {
    data object NavigateToHome : AuthEffect
    data object NavigateToLogin : AuthEffect
    data class ShowSnackbar(val message: String) : AuthEffect
}

sealed interface AuthPartialChange {
    data object Loading : AuthPartialChange
    data class SessionChanged(val session: AuthSession?) : AuthPartialChange
    data class Error(val error: AuthError) : AuthPartialChange
}

class AuthReducer {
    fun reduce(state: AuthState, change: AuthPartialChange): AuthState = when (change) {
        AuthPartialChange.Loading -> state.copy(isLoading = true, errorMessage = null)
        is AuthPartialChange.SessionChanged -> state.copy(
            isLoading = false,
            email = change.session?.user?.email,
            isAuthenticated = change.session != null,
            errorMessage = null,
        )
        is AuthPartialChange.Error -> state.copy(isLoading = false, errorMessage = change.error.message())
    }

    private fun AuthError.message(): String = when (this) {
        AuthError.InvalidCredentials -> "Invalid credentials"
        AuthError.MissingSession -> "No active session"
        is AuthError.Unknown -> message
    }
}
