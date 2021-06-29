package com.erdeanmich.todings.web.model

import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoPriority
import java.util.*

class ApiTransformer {
    fun localToApi(toDoItem: ToDoItem): ToDoApiModel {
        return ToDoApiModel(
            toDoItem.id,
            toDoItem.name,
            toDoItem.description,
            toDoItem.priority.toString(),
            toDoItem.isDone,
            toDoItem.deadline.time
        )
    }

    fun apiToLocal(toDoApiModel: ToDoApiModel): ToDoItem {
        val deadline = Date()
        deadline.time = toDoApiModel.expiry
        val priority = ToDoPriority.values()
            .find { it.toString() == toDoApiModel.priority  }?: ToDoPriority.NONE

        return ToDoItem(
            toDoApiModel.id,
            toDoApiModel.name,
            toDoApiModel.description,
            toDoApiModel.done,
            deadline,
            priority
        )
    }

}