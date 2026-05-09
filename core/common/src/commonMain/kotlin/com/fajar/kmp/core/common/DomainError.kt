package com.fajar.kmp.core.common

sealed interface DomainError {
    data object Offline : DomainError
    data object Unauthorized : DomainError
    data object NotFound : DomainError
    data class Validation(val message: String) : DomainError
    data class Unknown(val message: String) : DomainError
}

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val error: DomainError) : AppResult<Nothing>
}
