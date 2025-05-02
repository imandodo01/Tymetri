package com.millian.tymetri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.millian.tymetri.data.AppDatabase
import com.millian.tymetri.ui.common.TodoViewModelFactory
import com.millian.tymetri.ui.navigation.Screen
import com.millian.tymetri.ui.screens.detail.DetailScreen
import com.millian.tymetri.ui.screens.edit.EditTodoScreen
import com.millian.tymetri.ui.screens.home.HomeScreen
import com.millian.tymetri.ui.screens.home.HomeViewModel
import com.millian.tymetri.ui.theme.TymetriTheme

@Composable
fun TymetriApp() {
    val navController = rememberNavController()
    val db = AppDatabase.getInstance(LocalContext.current)
    val viewModel: HomeViewModel = viewModel(factory = TodoViewModelFactory(db.todoDao()))

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(Screen.AddTodo.route) },
                onTodoClick = { todo -> navController.navigate(Screen.Detail.createRoute(todo.id)) }
            )
        }
        composable(Screen.AddTodo.route) {
            EditTodoScreen(
                todoId = null,
                viewModel = viewModel,
//                onSave = { todo ->
////                    viewModel.updateTodo(todo.id, todo.title, todo.description, todo.isDone)
//                    viewModel.addTodo(todo.title, todo.description, todo.isDone)
//                    navController.popBackStack()
//                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("todoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getInt("todoId") ?: return@composable
            DetailScreen(
                todoId = todoId,
                viewModel = viewModel,
                onEditClick = { navController.navigate(Screen.Edit.createRoute(todoId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument("todoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getInt("todoId") ?: return@composable
            EditTodoScreen(
                todoId = todoId,
                viewModel = viewModel,
//                onSave = { todo ->
//                    viewModel.updateTodo(todo.id, todo.title, todo.description, todo.isDone)
//                    navController.popBackStack()
//                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JetSubmissionAppPreview() {
    TymetriTheme {
        TymetriApp()
    }
}
