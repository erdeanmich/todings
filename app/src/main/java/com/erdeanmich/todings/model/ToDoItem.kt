package com.erdeanmich.todings.model

import java.util.*

data class ToDoItem(
        var id: Int,
        var name: String,
        var description: String,
        var isDone: Boolean,
        var deadline: Date,
        var priority: ToDoPriority
)
