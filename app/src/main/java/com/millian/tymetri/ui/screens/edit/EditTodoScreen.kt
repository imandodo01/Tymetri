package com.millian.tymetri.ui.screens.edit

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.millian.tymetri.ui.screens.home.HomeViewModel

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todoId: Int?,
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    // State for loading indicator and form data
    var isLoading by remember { mutableStateOf(true) }

    // Get existing todo data (assuming you are using collectAsState)
    val existingTodo = todoId?.let { viewModel.getTodoById(it).collectAsState(initial = null) }?.value

    // Set loading to false once data is fetched
    LaunchedEffect(existingTodo, todoId) {
        if (todoId == null || existingTodo != null) {
            isLoading = false  // Set loading to false when data is available
        }
    }

    // Show CircularProgressIndicator while loading
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        // Form to either edit or add
        Log.e("testing", mutableStateOf(existingTodo?.title ?: "").toString())
        var title by remember { mutableStateOf(existingTodo?.title ?: "") }
        var description by remember { mutableStateOf(existingTodo?.description ?: "") }
        var isDone by remember { mutableStateOf(existingTodo?.isDone ?: false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (todoId == null) "Add Todo" else "Edit Todo") },
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (title.isNotBlank()) {
                                if (todoId == null) {
                                    viewModel.addTodo(title, description, isDone)
                                } else {
                                    viewModel.updateTodo(todoId, title, description, isDone)
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
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isDone, onCheckedChange = { isDone = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark as done")
                }
            }
        }
    }
}
