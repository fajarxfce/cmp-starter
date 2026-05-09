package com.fajar.kmp.feature.todo.data.sync

import com.fajar.kmp.core.network.NetworkMonitor
import com.fajar.kmp.core.sync.SyncManager
import com.fajar.kmp.core.sync.SyncStatus
import com.fajar.kmp.feature.todo.data.local.TodoLocalDataSource
import com.fajar.kmp.feature.todo.data.mapper.toDto
import com.fajar.kmp.feature.todo.data.mapper.toEntity
import com.fajar.kmp.feature.todo.data.remote.TodoRemoteDataSource

class TodoSyncManager(
    private val localDataSource: TodoLocalDataSource,
    private val remoteDataSource: TodoRemoteDataSource,
    private val networkMonitor: NetworkMonitor,
) : SyncManager {
    override suspend fun sync(): SyncStatus {
        if (!networkMonitor.isOnline()) return SyncStatus.Failed("Offline")

        val pending = localDataSource.pendingOperations()
        val changedTodos = pending.mapNotNull { localDataSource.findById(it.todoId) }
        remoteDataSource.pushTodos(changedTodos.map { it.toDto() })
        pending.forEach { localDataSource.deleteOperation(it.id) }
        remoteDataSource.fetchTodos().map { it.toEntity() }.forEach { localDataSource.upsert(it) }
        return SyncStatus.Synced
    }
}

class AlwaysOnlineNetworkMonitor : NetworkMonitor {
    override suspend fun isOnline(): Boolean = true
}
