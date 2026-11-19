package com.millian.tymetri.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.millian.tymetri.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE isArchived = 0 ORDER BY id DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE isArchived = 1 ORDER BY id DESC")
    fun getArchivedTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id AND isArchived = 0")
    fun getTodoById(id: Int): Flow<Todo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("UPDATE todos SET isArchived = 1 WHERE id = :id AND isArchived = 0")
    suspend fun archiveTodo(id: Int): Int

    @Query("UPDATE todos SET isArchived = 0 WHERE id = :id AND isArchived = 1")
    suspend fun restoreTodo(id: Int): Int
}
