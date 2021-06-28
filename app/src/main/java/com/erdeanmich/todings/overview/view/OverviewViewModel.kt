package com.erdeanmich.todings.overview.view

import android.app.Application
import androidx.lifecycle.*
import com.erdeanmich.todings.database.DatabaseProvider
import com.erdeanmich.todings.database.ToDoItemDAO
import com.erdeanmich.todings.model.ToDoItem
import kotlinx.coroutines.launch

class OverviewViewModel(application: Application) : AndroidViewModel(application) {
    private val toDoItemDao: ToDoItemDAO by lazy { DatabaseProvider.getToDoItemDAO(application) }
    private val toDoItems: MutableLiveData<List<ToDoItem>> by lazy {
        MutableLiveData<List<ToDoItem>>().also {
            loadToDoItems()
        }
    }

    fun getToDoItems(): LiveData<List<ToDoItem>> {
        loadToDoItems()
        return toDoItems
    }

    fun deleteToDoItems() {
        viewModelScope.launch {
            toDoItemDao.deleteAll()
            toDoItems.value = emptyList()
        }
    }

    private fun loadToDoItems() {
         viewModelScope.launch {
             toDoItems.value = toDoItemDao.getAll()
        }
    }

    fun update(toDoItem: ToDoItem) {
        viewModelScope.launch {
            toDoItemDao.update(toDoItem)
        }
    }

}
