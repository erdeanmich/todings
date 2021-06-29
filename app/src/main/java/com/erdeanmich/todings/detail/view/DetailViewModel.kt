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
import com.erdeanmich.todings.web.model.ApiTransformer
import com.erdeanmich.todings.web.service.ToDoService
import kotlinx.coroutines.launch
import java.util.*

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val toDoItemDao: ToDoItemDAO by lazy { DatabaseProvider.getToDoItemDAO(application) }
    private val toDoItem: MutableLiveData<ToDoItem> = MutableLiveData<ToDoItem>()
    private val transformer = ApiTransformer()
    private val service by lazy { ToDoService.getService() }


    fun getToDoItemById(id: Long?): LiveData<ToDoItem> {
        if (id != null) {
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
        if (toDoPriority == ToDoPriority.NONE && toDoItem.value?.priority != null && toDoItem.value?.priority != ToDoPriority.NONE) {
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
        toDoItem.value?.isDone = !(toDoItem.value?.isDone ?: false)
        return toDoItem.value?.isDone ?: false
    }

    fun save() {
        viewModelScope.launch {
            val item = toDoItem.value!!
            if (existingInDatabase()) {
                toDoItemDao.update(item)
                service.updateToDo(item.id, transformer.localToApi(item))
            } else {
                val id = toDoItemDao.insert(item)
                toDoItem.value?.id = id
                item.id = id
                service.createToDo(transformer.localToApi(item))
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            toDoItemDao.delete(toDoItem.value!!)
            service.deleteToDo(toDoItem.value!!.id)
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