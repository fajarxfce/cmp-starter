package com.fajar.kmp.feature.todo

import com.fajar.kmp.feature.todo.data.local.InMemoryTodoLocalDataSource
import com.fajar.kmp.feature.todo.data.remote.FakeTodoRemoteDataSource
import com.fajar.kmp.feature.todo.data.repository.TodoRepositoryImpl
import com.fajar.kmp.feature.todo.data.sync.AlwaysOnlineNetworkMonitor
import com.fajar.kmp.feature.todo.data.sync.TodoSyncManager
import com.fajar.kmp.feature.todo.domain.usecase.AddTodoUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class TodoUseCaseTest {
    @Test
    fun add_todo_trims_title() = runTest {
        val local = InMemoryTodoLocalDataSource()
        val repository = TodoRepositoryImpl(local, TodoSyncManager(local, FakeTodoRemoteDataSource(), AlwaysOnlineNetworkMonitor()))
        val useCase = AddTodoUseCase(repository)

        val todo = useCase("  Trim me  ")

        assertEquals("Trim me", todo.title)
    }
}
