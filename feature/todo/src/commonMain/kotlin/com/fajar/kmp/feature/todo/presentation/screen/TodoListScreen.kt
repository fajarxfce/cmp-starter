package com.fajar.kmp.feature.todo.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fajar.kmp.core.designsystem.DashboardCard
import com.fajar.kmp.core.designsystem.EmptyState
import com.fajar.kmp.core.designsystem.ErrorState
import com.fajar.kmp.core.designsystem.LoadingState
import com.fajar.kmp.core.designsystem.PrimaryButton
import com.fajar.kmp.feature.todo.presentation.contract.TodoIntent
import com.fajar.kmp.feature.todo.presentation.contract.TodoState
import com.fajar.kmp.feature.todo.presentation.contract.TodoUiModel

@Composable
fun TodoListScreen(
    state: TodoState,
    onIntent: (TodoIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Local-first Todos", style = MaterialTheme.typography.headlineSmall)
        Text("Clean Architecture + MVI sample feature", color = MaterialTheme.colorScheme.onSurfaceVariant)
        AddTodoCard(onIntent)
        if (state.isLoading) LoadingState()
        state.errorMessage?.let { ErrorState(it) }
        if (state.todos.isEmpty() && !state.isLoading) {
            EmptyState("No todos yet", "Add your first todo to test the full MVI flow.")
        } else {
            state.todos.forEach { todo -> TodoRow(todo, onIntent) }
        }
        Text("Sync: ${state.syncStatus}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        PrimaryButton("Sync now", onClick = { onIntent(TodoIntent.Refresh) })
    }
}

@Composable
private fun AddTodoCard(onIntent: (TodoIntent) -> Unit) {
    var title by remember { mutableStateOf("") }
    DashboardCard {
        Text("Create todo", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Title") },
        )
        Spacer(Modifier.height(8.dp))
        PrimaryButton("Add", onClick = {
            onIntent(TodoIntent.AddTodo(title))
            title = ""
        })
    }
}

@Composable
private fun TodoRow(todo: TodoUiModel, onIntent: (TodoIntent) -> Unit) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onIntent(TodoIntent.ToggleTodo(todo.id)) },
            )
            Column(Modifier.weight(1f)) {
                Text(todo.title, style = MaterialTheme.typography.titleMedium)
                if (todo.isDirty) Text("Pending sync", color = MaterialTheme.colorScheme.primary)
            }
            PrimaryButton("Delete", onClick = { onIntent(TodoIntent.DeleteTodo(todo.id)) })
        }
    }
}
