package com.fajar.kmp.core.common.result

import kotlin.coroutines.cancellation.CancellationException

sealed class Try<out S, out E> {
    data class Success<out S>(val result: S) : Try<S, Nothing>()
    data class Error<out E>(val result: E) : Try<Nothing, E>()
}

typealias VoidTry<E> = Try<Unit, E>

@Suppress("TooGenericExceptionCaught")
inline fun <S, R> S.runOperationCatching(block: S.() -> R): Try<R, Throwable> = try {
    Try.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Try.Error(e)
}

inline fun <S, E> Try<S, E>.doOnSuccess(block: (S) -> Unit): Try<S, E> {
    if (this is Try.Success) block(result)
    return this
}

inline fun <S, E> Try<S, E>.doOnError(block: (E) -> Unit): Try<S, E> {
    if (this is Try.Error) block(result)
    return this
}

inline fun <S, E, R> Try<S, E>.mapSuccess(block: (S) -> R): Try<R, E> = when (this) {
    is Try.Success -> Try.Success(block(result))
    is Try.Error -> Try.Error(result)
}

inline fun <S, E, R> Try<S, E>.mapError(block: (E) -> R): Try<S, R> = when (this) {
    is Try.Success -> Try.Success(result)
    is Try.Error -> Try.Error(block(result))
}

inline fun <S, E, R> Try<S, E>.mapNestedSuccess(block: (S) -> Try<R, E>): Try<R, E> = when (this) {
    is Try.Success -> block(result)
    is Try.Error -> Try.Error(result)
}
