package com.fajar.kmp.feature.auth

import com.fajar.kmp.feature.auth.domain.model.AuthError
import com.fajar.kmp.feature.auth.domain.model.AuthSession
import com.fajar.kmp.feature.auth.domain.model.AuthTokens
import com.fajar.kmp.feature.auth.domain.model.AuthUser
import com.fajar.kmp.feature.auth.domain.model.UserId
import com.fajar.kmp.feature.auth.presentation.contract.AuthPartialChange
import com.fajar.kmp.feature.auth.presentation.contract.AuthReducer
import com.fajar.kmp.feature.auth.presentation.contract.AuthState
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthReducerTest {
    private val reducer = AuthReducer()

    @Test
    fun session_changed_marks_user_authenticated() {
        val session = AuthSession(
            user = AuthUser(UserId("user-1"), "user@example.com", "User"),
            tokens = AuthTokens("access", "refresh"),
        )

        val state = reducer.reduce(AuthState(isLoading = true), AuthPartialChange.SessionChanged(session))

        assertEquals(false, state.isLoading)
        assertEquals(true, state.isAuthenticated)
        assertEquals("user@example.com", state.email)
    }

    @Test
    fun invalid_credentials_maps_to_message() {
        val state = reducer.reduce(AuthState(isLoading = true), AuthPartialChange.Error(AuthError.InvalidCredentials))

        assertEquals(false, state.isLoading)
        assertEquals("Invalid credentials", state.errorMessage)
    }
}
