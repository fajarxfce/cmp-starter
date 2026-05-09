package com.fajar.kmp.feature.todo.domain.repository

import com.fajar.kmp.core.model.Todo
import com.fajar.kmp.core.model.TodoId
import com.fajar.kmp.core.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeTodos(): Flow<List<Todo>>
    suspend fun addTodo(title: String): Todo
    suspend fun toggleTodo(id: TodoId): Todo?
    suspend fun deleteTodo(id: TodoId)
    suspend fun syncTodos(): SyncStatus
}
