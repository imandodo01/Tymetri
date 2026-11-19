package com.millian.tymetri

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.millian.tymetri.data.AppDatabase
import com.millian.tymetri.model.Todo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoArchiveTest {

    @Test
    fun archiveAndRestoreMoveTodoBetweenActiveAndArchivedLists() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        try {
            val dao = database.todoDao()
            dao.insertTodo(Todo(title = "Prepare release notes"))

            val todo = dao.getAllTodos().first().single()
            assertFalse(todo.isArchived)

            dao.archiveTodo(todo.id)

            assertEquals(0, dao.getAllTodos().first().size)
            assertEquals(todo.id, dao.getArchivedTodos().first().single().id)

            dao.restoreTodo(todo.id)

            assertEquals(todo.id, dao.getAllTodos().first().single().id)
            assertEquals(0, dao.getArchivedTodos().first().size)
        } finally {
            database.close()
        }
    }

    @Test
    fun completionCanBeToggledWithoutChangingTaskIdentity() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        try {
            val dao = database.todoDao()
            dao.insertTodo(Todo(title = "Complete a task"))

            val original = dao.getAllTodos().first().single()
            dao.updateTodo(original.copy(isDone = true))

            val completed = dao.getAllTodos().first().single()
            assertTrue(completed.isDone)
            assertEquals(original.id, completed.id)

            dao.updateTodo(completed.copy(isDone = false))

            val reopened = dao.getAllTodos().first().single()
            assertFalse(reopened.isDone)
            assertEquals(original.id, reopened.id)
        } finally {
            database.close()
        }
    }

    @Test
    fun versionOneDatabaseMigratesWithoutArchivingExistingTodos() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseName = "todo-archive-migration-test"
        context.deleteDatabase(databaseName)

        val legacyDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        legacyDatabase.execSQL(
            """
            CREATE TABLE IF NOT EXISTS todos (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                isDone INTEGER NOT NULL
            )
            """.trimIndent()
        )
        legacyDatabase.execSQL(
            "INSERT INTO todos (title, description, isDone) VALUES (?, ?, ?)",
            arrayOf("Existing task", "Keep this task", 0)
        )
        legacyDatabase.version = 1
        legacyDatabase.close()

        val migratedDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()

        try {
            val todos = migratedDatabase.todoDao().getAllTodos().first()
            assertEquals(1, todos.size)
            assertEquals("Existing task", todos.single().title)
            assertFalse(todos.single().isArchived)
        } finally {
            migratedDatabase.close()
            context.deleteDatabase(databaseName)
        }
    }
}
