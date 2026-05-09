package com.fajar.kmp.feature.todo.presentation.contract

import com.fajar.kmp.core.common.DomainError
import com.fajar.kmp.core.model.Todo
import com.fajar.kmp.core.sync.SyncStatus
import kotlinx.collections.immutable.toImmutableList

sealed interface TodoPartialChange {
    data object Loading : TodoPartialChange
    data class TodosLoaded(val todos: List<Todo>) : TodoPartialChange
    data class Error(val error: DomainError) : TodoPartialChange
    data class SyncStatusChanged(val status: SyncStatus) : TodoPartialChange
}

interface Reducer<S, C> {
    fun reduce(state: S, change: C): S
}

class TodoReducer : Reducer<TodoState, TodoPartialChange> {
    override fun reduce(state: TodoState, change: TodoPartialChange): TodoState = when (change) {
        TodoPartialChange.Loading -> state.copy(isLoading = true, errorMessage = null)
        is TodoPartialChange.TodosLoaded -> state.copy(
            isLoading = false,
            todos = change.todos.map { todo ->
                TodoUiModel(
                    id = todo.id,
                    title = todo.title,
                    isCompleted = todo.isCompleted,
                    isDirty = todo.isDirty,
                )
            }.toImmutableList(),
            errorMessage = null,
        )
        is TodoPartialChange.Error -> state.copy(isLoading = false, errorMessage = change.error.message())
        is TodoPartialChange.SyncStatusChanged -> state.copy(syncStatus = change.status.toUi())
    }

    private fun DomainError.message(): String = when (this) {
        DomainError.Offline -> "You are offline. Changes will sync later."
        DomainError.Unauthorized -> "Please sign in again."
        DomainError.NotFound -> "The requested item was not found."
        is DomainError.Validation -> message
        is DomainError.Unknown -> message
    }

    private fun SyncStatus.toUi(): SyncStatusUi = when (this) {
        SyncStatus.Idle -> SyncStatusUi.Idle
        SyncStatus.Syncing -> SyncStatusUi.Syncing
        SyncStatus.Synced -> SyncStatusUi.Synced
        is SyncStatus.Failed -> SyncStatusUi.Failed
    }
}
