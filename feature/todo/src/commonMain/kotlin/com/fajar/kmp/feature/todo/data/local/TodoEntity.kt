package com.fajar.kmp.feature.todo.data.local

data class TodoEntity(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val isDirty: Boolean,
    val isDeleted: Boolean,
    val localVersion: Long,
    val serverVersion: Long?,
    val lastSyncedAtEpochMilliseconds: Long?,
)

data class TodoSyncOperationEntity(
    val id: String,
    val todoId: String,
    val type: TodoSyncOperationType,
    val idempotencyKey: String,
    val retryCount: Int,
    val lastError: String?,
)

enum class TodoSyncOperationType { Create, Update, Delete }

interface TodoLocalDataSource {
    fun observeTodos(): kotlinx.coroutines.flow.Flow<List<TodoEntity>>
    suspend fun upsert(entity: TodoEntity)
    suspend fun findById(id: String): TodoEntity?
    suspend fun markDeleted(id: String)
    suspend fun pendingOperations(): List<TodoSyncOperationEntity>
    suspend fun enqueue(operation: TodoSyncOperationEntity)
    suspend fun deleteOperation(id: String)
}
