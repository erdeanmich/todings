package com.erdeanmich.todings.web.model

data class ToDoApiModel (
    var id: Long,
    var name: String,
    var description: String,
    var priority: String,
    var done: Boolean,
    var expiry: Long
){}
