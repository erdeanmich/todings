package com.erdeanmich.todings.overview.view

import android.app.Application
import androidx.lifecycle.*
import com.erdeanmich.todings.database.DatabaseProvider
import com.erdeanmich.todings.database.ToDoItemDAO
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoOverviewSortMode
import kotlinx.coroutines.launch
import java.util.*

class OverviewViewModel(application: Application) : AndroidViewModel(application) {
    private var currentSortMode = ToDoOverviewSortMode.PRIORITY_DATE
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
            toDoItems.value = loadSortedToDoItems()
        }
    }

    private suspend fun loadSortedToDoItems(): List<ToDoItem> {
        val toDoItemsPerDoneStatus = toDoItemDao.getAll()
            .groupBy { it.isDone }
            .toSortedMap(compareBy { it })

        return toDoItemsPerDoneStatus.mapValues { (_, toDos) ->
            return@mapValues when (currentSortMode) {
                ToDoOverviewSortMode.PRIORITY_DATE -> sortByPriorityAndDate(toDos)
                ToDoOverviewSortMode.DATE_PRIORITY -> sortByDateAndPriority(toDos)
            }
        }
            .flatMap { it.value }
    }

    fun update(toDoItem: ToDoItem){
        viewModelScope.launch {
            toDoItemDao.update(toDoItem)
            toDoItems.value = loadSortedToDoItems()
        }
    }

    private fun sortByPriorityAndDate(toDoItems: List<ToDoItem>): List<ToDoItem> {
        return toDoItems.groupBy { it.priority }
            .toSortedMap(compareByDescending { it.order })
            .mapValues { entry ->
                val todos = entry.value
                todos.sortedBy { it.deadline }
            }
            .flatMap { it.value }
    }

    private fun sortByDateAndPriority(toDoItems: List<ToDoItem>): List<ToDoItem> {
        return toDoItems.groupBy { item ->
            val c = Calendar.getInstance()
            c.time = item.deadline
            c.set(Calendar.SECOND,0)
            c.set(Calendar.MILLISECOND, 0)
            return@groupBy c
        }
            .toSortedMap(compareBy { it })
            .mapValues { (key, toDos) ->
                println(key)
                return@mapValues toDos.sortedByDescending { it.priority.order }
            }
            .flatMap { it.value }
    }

    fun setSortMode(sortMode: ToDoOverviewSortMode) {
        currentSortMode = sortMode
        loadToDoItems()
    }

}
