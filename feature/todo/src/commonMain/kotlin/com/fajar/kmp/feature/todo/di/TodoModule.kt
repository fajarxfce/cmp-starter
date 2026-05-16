package com.fajar.kmp.feature.todo.di

import com.fajar.kmp.core.network.NetworkMonitor
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
import org.koin.dsl.module

val todoModule = module {
    single<TodoLocalDataSource> { InMemoryTodoLocalDataSource() }
    single<TodoRemoteDataSource> { FakeTodoRemoteDataSource() }
    single<NetworkMonitor> { AlwaysOnlineNetworkMonitor() }
    single { TodoSyncManager(get<TodoLocalDataSource>(), get<TodoRemoteDataSource>(), get<NetworkMonitor>()) }
    single<TodoRepository> { TodoRepositoryImpl(get<TodoLocalDataSource>(), get<TodoSyncManager>()) }
    single { ObserveTodosUseCase(get<TodoRepository>()) }
    single { AddTodoUseCase(get<TodoRepository>()) }
    single { ToggleTodoUseCase(get<TodoRepository>()) }
    single { DeleteTodoUseCase(get<TodoRepository>()) }
    single { SyncTodosUseCase(get<TodoRepository>()) }
    single {
        TodoViewModel(
            observeTodos = get<ObserveTodosUseCase>(),
            addTodo = get<AddTodoUseCase>(),
            toggleTodo = get<ToggleTodoUseCase>(),
            deleteTodo = get<DeleteTodoUseCase>(),
            syncTodos = get<SyncTodosUseCase>(),
        )
    }
}
