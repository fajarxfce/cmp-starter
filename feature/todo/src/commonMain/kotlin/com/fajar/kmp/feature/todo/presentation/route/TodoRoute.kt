package com.fajar.kmp.feature.todo.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.fajar.kmp.feature.todo.presentation.contract.TodoIntent
import com.fajar.kmp.feature.todo.presentation.screen.TodoListScreen
import org.koin.compose.koinInject

@Composable
fun TodoRoute(viewModel: com.fajar.kmp.feature.todo.presentation.viewmodel.TodoViewModel = koinInject()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.onIntent(TodoIntent.LoadTodos)
    }

    TodoListScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
