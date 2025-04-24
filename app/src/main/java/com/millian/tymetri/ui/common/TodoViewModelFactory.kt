package com.millian.tymetri.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.millian.tymetri.data.dao.TodoDao
import com.millian.tymetri.ui.screens.home.HomeViewModel

class TodoViewModelFactory(private val dao: TodoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(dao) as T
    }
}
