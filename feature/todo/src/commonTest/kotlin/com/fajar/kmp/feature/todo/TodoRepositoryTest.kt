package com.fajar.kmp.feature.todo

import app.cash.turbine.test
import com.fajar.kmp.feature.todo.data.local.InMemoryTodoLocalDataSource
import com.fajar.kmp.feature.todo.data.remote.FakeTodoRemoteDataSource
import com.fajar.kmp.feature.todo.data.repository.TodoRepositoryImpl
import com.fajar.kmp.feature.todo.data.sync.AlwaysOnlineNetworkMonitor
import com.fajar.kmp.feature.todo.data.sync.TodoSyncManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class TodoRepositoryTest {
    @Test
    fun add_todo_writes_to_local_source_first() = runTest {
        val local = InMemoryTodoLocalDataSource()
        val sync = TodoSyncManager(local, FakeTodoRemoteDataSource(), AlwaysOnlineNetworkMonitor())
        val repository = TodoRepositoryImpl(local, sync)

        repository.observeTodos().test {
            assertEquals(emptyList(), awaitItem())
            repository.addTodo("Local first")
            assertEquals("Local first", awaitItem().single().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
