package com.fajar.kmp.core.testing

import com.fajar.kmp.core.common.DispatcherProvider
import com.fajar.kmp.core.model.Todo
import com.fajar.kmp.core.model.TodoId
import com.fajar.kmp.core.network.NetworkMonitor
import com.fajar.kmp.core.sync.SyncStatus
import com.fajar.kmp.feature.todo.domain.repository.TodoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant

class TestDispatcherProvider(
    override val default: CoroutineDispatcher = Dispatchers.Default,
    override val io: CoroutineDispatcher = Dispatchers.Default,
    override val main: CoroutineDispatcher = Dispatchers.Default,
) : DispatcherProvider

class FakeNetworkMonitor(private val online: Boolean = true) : NetworkMonitor {
    override suspend fun isOnline(): Boolean = online
}

interface FakeAuthRepository {
    suspend fun isSignedIn(): Boolean
}

class FakeTodoRepository(initialTodos: List<Todo> = emptyList()) : TodoRepository {
    private val todos = MutableStateFlow(initialTodos)

    override fun observeTodos(): Flow<List<Todo>> = todos

    override suspend fun addTodo(title: String): Todo {
        val todo = sampleTodo(title = title)
        todos.value = todos.value + todo
        return todo
    }

    override suspend fun toggleTodo(id: TodoId): Todo? {
        val current = todos.value.firstOrNull { it.id == id } ?: return null
        val updated = current.copy(isCompleted = !current.isCompleted)
        todos.value = todos.value.map { if (it.id == id) updated else it }
        return updated
    }

    override suspend fun deleteTodo(id: TodoId) {
        todos.value = todos.value.filterNot { it.id == id }
    }

    override suspend fun syncTodos(): SyncStatus = SyncStatus.Synced
}

fun sampleTodo(
    id: TodoId = TodoId("todo-1"),
    title: String = "Ship starter kit",
    isCompleted: Boolean = false,
): Todo = Todo(
    id = id,
    title = title,
    isCompleted = isCompleted,
    isDirty = false,
    isDeleted = false,
    localVersion = 1,
    serverVersion = 1,
    lastSyncedAt = Instant.fromEpochMilliseconds(0),
)
