package com.erdeanmich.todings.detail.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.erdeanmich.todings.database.DatabaseProvider
import com.erdeanmich.todings.database.ToDoItemDAO
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoPriority
import kotlinx.coroutines.launch
import java.util.*

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val toDoItemDao: ToDoItemDAO by lazy { DatabaseProvider.getToDoItemDAO(application) }
    private val toDoItem: MutableLiveData<ToDoItem> = MutableLiveData<ToDoItem>()


    fun getToDoItemById(id: Long?): LiveData<ToDoItem> {
        if(id != null) {
            getToDoItemByIdFromDatabase(id)
        } else {
            val date = Calendar.getInstance()
            date.time = Date()
            date.set(Calendar.SECOND, 0)
            toDoItem.value = ToDoItem(0, "", "", false, date.time, ToDoPriority.NONE)
        }
        return toDoItem
    }

    fun setName(name: String) {
        toDoItem.value?.name = name
    }

    fun setPriority(toDoPriority: ToDoPriority) {
        if(toDoPriority == ToDoPriority.NONE && toDoItem.value?.priority != null && toDoItem.value?.priority != ToDoPriority.NONE) {
            return
        }

        toDoItem.value?.priority = toDoPriority
    }

    fun setDescription(description: String) {
        toDoItem.value?.description = description
    }

    fun setDeadline(deadline: Date) {
        toDoItem.value?.deadline = deadline
    }

    fun invertIsDone(): Boolean {
        toDoItem.value?.isDone = !(toDoItem.value?.isDone?: false)
        return toDoItem.value?.isDone?: false
    }

    fun save() {
        viewModelScope.launch {
            if (existingInDatabase()) {
                toDoItemDao.update(toDoItem.value!!)
            } else {
                val id = toDoItemDao.insert(toDoItem.value!!)
                toDoItem.value?.id = id
            }
        }
    }

    fun delete() {
        viewModelScope.launch {

        }
    }

    fun existingInDatabase(): Boolean {
        return toDoItem.value?.id != 0L
    }

    private fun getToDoItemByIdFromDatabase(id: Long) {
        viewModelScope.launch {
            toDoItem.value = toDoItemDao.getById(id)
        }
    }
}