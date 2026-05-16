package com.fajar.kmp.feature.auth

import com.fajar.kmp.core.datastore.InMemoryKeyValueStore
import com.fajar.kmp.core.datastore.KeyValueSessionPreferences
import com.fajar.kmp.feature.auth.data.AuthRepositoryImpl
import com.fajar.kmp.feature.auth.domain.model.AuthResult
import com.fajar.kmp.feature.auth.domain.usecase.LoginUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class AuthUseCaseTest {
    @Test
    fun login_trims_email_before_repository_call() = runTest {
        val useCase = LoginUseCase(AuthRepositoryImpl(KeyValueSessionPreferences(InMemoryKeyValueStore())))

        val result = useCase("  user@example.com  ", "password123")

        assertTrue(result is AuthResult.Success)
        assertEquals("user@example.com", (result as AuthResult.Success).value.user.email)
    }
}
