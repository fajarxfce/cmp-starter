package com.fajar.kmp.feature.todo.di

import com.fajar.kmp.feature.todo.data.local.InMemoryTodoLocalDataSource
import com.fajar.kmp.feature.todo.data.local.TodoLocalDataSource
import com.fajar.kmp.feature.todo.data.remote.FakeTodoRemoteDataSource
import com.fajar.kmp.feature.todo.data.remote.TodoRemoteDataSource
import com.fajar.kmp.feature.todo.data.repository.TodoRepositoryImpl
import com.fajar.kmp.feature.todo.data.sync.AlwaysOnlineNetworkMonitor
import com.fajar.kmp.feature.todo.data.sync.TodoSyncManager
import com.fajar.kmp.feature.todo.domain.repository.TodoRepository
import com.fajar.kmp.feature.todo.domain.usecase.AddTodoUseCase
import com.fajar.kmp.feature.todo.domain.usecase.DeleteTodoUseCase
import com.fajar.kmp.feature.todo.domain.usecase.ObserveTodosUseCase
import com.fajar.kmp.feature.todo.domain.usecase.SyncTodosUseCase
import com.fajar.kmp.feature.todo.domain.usecase.ToggleTodoUseCase
import com.fajar.kmp.feature.todo.presentation.viewmodel.TodoViewModel

class TodoGraph {
    private val localDataSource: TodoLocalDataSource = InMemoryTodoLocalDataSource()
    private val remoteDataSource: TodoRemoteDataSource = FakeTodoRemoteDataSource()
    private val syncManager = TodoSyncManager(localDataSource, remoteDataSource, AlwaysOnlineNetworkMonitor())
    private val repository: TodoRepository = TodoRepositoryImpl(localDataSource, syncManager)

    fun viewModel(): TodoViewModel = TodoViewModel(
        observeTodos = ObserveTodosUseCase(repository),
        addTodo = AddTodoUseCase(repository),
        toggleTodo = ToggleTodoUseCase(repository),
        deleteTodo = DeleteTodoUseCase(repository),
        syncTodos = SyncTodosUseCase(repository),
    )
}

fun todoModuleDescription(): String = "Koin-ready Todo module: data sources, repository, use cases, ViewModel"
