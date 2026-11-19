package com.millian.tymetri.ui.screens.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.millian.tymetri.ui.screens.home.HomeViewModel
import com.millian.tymetri.ui.screens.home.TodoLoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todoId: Int?,
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val loadState by produceState<TodoLoadState>(
        initialValue = if (todoId == null) {
            TodoLoadState.Loaded(null)
        } else {
            TodoLoadState.Loading
        },
        key1 = todoId
    ) {
        if (todoId != null) {
            viewModel.getTodoById(todoId).collect { todo ->
                value = TodoLoadState.Loaded(todo)
            }
        }
    }

    when (val state = loadState) {
        TodoLoadState.Loading -> Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        is TodoLoadState.Loaded -> {
            val existingTodo = state.todo

            if (todoId != null && existingTodo == null) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Edit Todo") },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Todo not found or is archived")
                    }
                }
            } else {
                var title by remember(todoId, existingTodo?.id) {
                    mutableStateOf(existingTodo?.title.orEmpty())
                }
                var description by remember(todoId, existingTodo?.id) {
                    mutableStateOf(existingTodo?.description.orEmpty())
                }
                var isDone by remember(todoId, existingTodo?.id) {
                    mutableStateOf(existingTodo?.isDone ?: false)
                }
                var titleError by remember(todoId, existingTodo?.id) {
                    mutableStateOf(false)
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (todoId == null) "Add Todo" else "Edit Todo") },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    if (title.isBlank()) {
                                        titleError = true
                                    } else {
                                        if (todoId == null) {
                                            viewModel.addTodo(title.trim(), description, isDone)
                                        } else {
                                            viewModel.updateTodo(todoId, title.trim(), description, isDone)
                                        }
                                        onBack()
                                    }
                                }) {
                                    Icon(Icons.Default.Check, contentDescription = "Save")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier.padding(padding).padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                if (it.isNotBlank()) titleError = false
                            },
                            label = { Text("Title") },
                            isError = titleError,
                            supportingText = {
                                if (titleError) Text("Title is required")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isDone, onCheckedChange = { isDone = it })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mark as done")
                        }
                    }
                }
            }
        }
    }
}
