package com.millian.tymetri.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millian.tymetri.data.dao.TodoDao
import com.millian.tymetri.model.Todo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val dao: TodoDao) : ViewModel() {

    val todos = dao.getAllTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedTodos = dao.getArchivedTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTodoById(id: Int): StateFlow<Todo?> {
        return dao.getTodoById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun addTodo(title: String, description: String?, isDone: Boolean) {
        viewModelScope.launch {
            dao.insertTodo(Todo(title = title, description = description, isDone = isDone))
        }
    }

    fun updateTodo(id: Int, title: String, description: String?, isDone: Boolean) {
        viewModelScope.launch {
            dao.updateTodo(Todo(id = id, title = title, description = description, isDone = isDone))
        }
    }

    fun toggleDone(todo: Todo) {
        viewModelScope.launch {
            dao.updateTodo(todo.copy(isDone = !todo.isDone))
        }
    }

    fun archiveTodo(todo: Todo) {
        viewModelScope.launch {
            dao.archiveTodo(todo.id)
        }
    }

    fun restoreTodo(id: Int) {
        viewModelScope.launch {
            dao.restoreTodo(id)
        }
    }
}
