package com.fajar.kmp.feature.todo.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.fajar.kmp.feature.todo.di.TodoGraph
import com.fajar.kmp.feature.todo.presentation.contract.TodoIntent
import com.fajar.kmp.feature.todo.presentation.screen.TodoListScreen

@Composable
fun TodoRoute(graph: TodoGraph = remember { TodoGraph() }) {
    val viewModel = remember { graph.viewModel() }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.onIntent(TodoIntent.LoadTodos)
    }

    TodoListScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
