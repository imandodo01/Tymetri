package com.millian.tymetri.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddTodo : Screen("add_todo")
    object Detail : Screen("detail/{todoId}") {
        fun createRoute(todoId: Int) = "detail/$todoId"
    }
    object Edit : Screen("edit/{todoId}") {
        fun createRoute(todoId: Int) = "edit/$todoId"
    }
}
