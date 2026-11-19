package com.millian.tymetri.ui.screens.home

import com.millian.tymetri.model.Todo

sealed interface TodoLoadState {
    data object Loading : TodoLoadState
    data class Loaded(val todo: Todo?) : TodoLoadState
}
