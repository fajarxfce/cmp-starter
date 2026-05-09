package com.fajar.kmp.feature.todo.presentation.contract

import com.fajar.kmp.core.model.TodoId

sealed interface TodoIntent {
    data object LoadTodos : TodoIntent
    data class AddTodo(val title: String) : TodoIntent
    data class ToggleTodo(val id: TodoId) : TodoIntent
    data class DeleteTodo(val id: TodoId) : TodoIntent
    data class OpenDetail(val id: TodoId) : TodoIntent
    data object Refresh : TodoIntent
}
