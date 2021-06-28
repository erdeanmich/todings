package com.erdeanmich.todings.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ToDoItem(
        @PrimaryKey(autoGenerate = true) var id: Long,
        var name: String,
        var description: String,
        var isDone: Boolean,
        var deadline: Date,
        var priority: ToDoPriority
)
