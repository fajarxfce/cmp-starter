package com.fajar.kmp.feature.todo.presentation.contract

import com.fajar.kmp.core.model.TodoId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TodoUiModel(
    val id: TodoId,
    val title: String,
    val isCompleted: Boolean,
    val isDirty: Boolean,
)

data class TodoState(
    val isLoading: Boolean = false,
    val todos: ImmutableList<TodoUiModel> = persistentListOf(),
    val errorMessage: String? = null,
    val syncStatus: SyncStatusUi = SyncStatusUi.Idle,
)

enum class SyncStatusUi { Idle, Syncing, Synced, Failed }
