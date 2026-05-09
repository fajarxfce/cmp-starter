package com.fajar.kmp.feature.todo.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.map

class InMemoryTodoLocalDataSource : TodoLocalDataSource {
    private val todos = MutableStateFlow<List<TodoEntity>>(emptyList())
    private val operations = mutableListOf<TodoSyncOperationEntity>()

    override fun observeTodos(): Flow<List<TodoEntity>> = todos.map { entries -> entries.filterNot { it.isDeleted } }

    override suspend fun upsert(entity: TodoEntity) {
        todos.value = todos.value.filterNot { it.id == entity.id } + entity
    }

    override suspend fun findById(id: String): TodoEntity? = todos.value.firstOrNull { it.id == id }

    override suspend fun markDeleted(id: String) {
        val current = findById(id) ?: return
        upsert(current.copy(isDeleted = true, isDirty = true, localVersion = current.localVersion + 1))
    }

    override suspend fun pendingOperations(): List<TodoSyncOperationEntity> = operations.toList()

    override suspend fun enqueue(operation: TodoSyncOperationEntity) {
        operations += operation
    }

    override suspend fun deleteOperation(id: String) {
        operations.removeAll { it.id == id }
    }
}
