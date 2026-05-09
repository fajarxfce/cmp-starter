package com.fajar.kmp.feature.todo.data.repository

import com.fajar.kmp.core.model.Todo
import com.fajar.kmp.core.model.TodoId
import com.fajar.kmp.core.sync.SyncManager
import com.fajar.kmp.core.sync.SyncStatus
import com.fajar.kmp.feature.todo.data.local.TodoEntity
import com.fajar.kmp.feature.todo.data.local.TodoLocalDataSource
import com.fajar.kmp.feature.todo.data.local.TodoSyncOperationEntity
import com.fajar.kmp.feature.todo.data.local.TodoSyncOperationType
import com.fajar.kmp.feature.todo.data.mapper.toDomain
import com.fajar.kmp.feature.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class TodoRepositoryImpl(
    private val localDataSource: TodoLocalDataSource,
    private val syncManager: SyncManager,
) : TodoRepository {
    override fun observeTodos(): Flow<List<Todo>> = localDataSource.observeTodos().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun addTodo(title: String): Todo {
        val id = TodoId(newId())
        val entity = TodoEntity(
            id = id.value,
            title = title,
            isCompleted = false,
            isDirty = true,
            isDeleted = false,
            localVersion = 1,
            serverVersion = null,
            lastSyncedAtEpochMilliseconds = null,
        )
        localDataSource.upsert(entity)
        localDataSource.enqueue(operation(entity.id, TodoSyncOperationType.Create))
        return entity.toDomain()
    }

    override suspend fun toggleTodo(id: TodoId): Todo? {
        val current = localDataSource.findById(id.value) ?: return null
        val updated = current.copy(
            isCompleted = !current.isCompleted,
            isDirty = true,
            localVersion = current.localVersion + 1,
        )
        localDataSource.upsert(updated)
        localDataSource.enqueue(operation(updated.id, TodoSyncOperationType.Update))
        return updated.toDomain()
    }

    override suspend fun deleteTodo(id: TodoId) {
        localDataSource.markDeleted(id.value)
        localDataSource.enqueue(operation(id.value, TodoSyncOperationType.Delete))
    }

    override suspend fun syncTodos(): SyncStatus = syncManager.sync()

    private fun operation(todoId: String, type: TodoSyncOperationType): TodoSyncOperationEntity = TodoSyncOperationEntity(
        id = newId(),
        todoId = todoId,
        type = type,
        idempotencyKey = newId(),
        retryCount = 0,
        lastError = null,
    )

    private fun newId(): String = Random.nextLong().toString(radix = 16).removePrefix("-")
}
