package com.fajar.kmp.core.sync

sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Syncing : SyncStatus
    data class Failed(val reason: String) : SyncStatus
    data object Synced : SyncStatus
}

interface SyncManager {
    suspend fun sync(): SyncStatus
}
