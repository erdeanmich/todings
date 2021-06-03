package com.erdeanmich.todings.model

import java.util.*

data class ToDoItem(
        val id: Int,
        val name: String,
        val description: String,
        val isDone: Boolean,
        val deadline: Date,
        val priority: ToDoPriority
)
