package com.millian.tymetri.ui.screens.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.millian.tymetri.ui.screens.home.HomeViewModel
import com.millian.tymetri.ui.screens.home.TodoLoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    todoId: Int,
    viewModel: HomeViewModel,
    onEditClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val loadState by produceState<TodoLoadState>(
        initialValue = TodoLoadState.Loading,
        key1 = todoId
    ) {
        viewModel.getTodoById(todoId).collect { todo ->
            value = TodoLoadState.Loaded(todo)
        }
    }

    val loadedTodo = (loadState as? TodoLoadState.Loaded)?.todo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (loadedTodo != null) {
                        IconButton(onClick = { onEditClick(todoId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (loadState) {
            TodoLoadState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            is TodoLoadState.Loaded -> {
                val item = loadedTodo
                if (item == null) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Todo not found")
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(padding).padding(16.dp)
                    ) {
                        Text(text = item.title, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.description ?: "No description", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Completed: ${if (item.isDone) "Yes" else "No"}")
                    }
                }
            }
        }
    }
}
