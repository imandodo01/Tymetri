package com.millian.tymetri.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = "",
    val isDone: Boolean = false,
    @ColumnInfo(defaultValue = "0") val isArchived: Boolean = false
)
