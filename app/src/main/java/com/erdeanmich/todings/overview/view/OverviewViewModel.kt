package com.erdeanmich.todings.overview.view

import android.app.Application
import androidx.lifecycle.*
import com.erdeanmich.todings.database.DatabaseProvider
import com.erdeanmich.todings.database.ToDoItemDAO
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoOverviewSortMode
import com.erdeanmich.todings.web.model.ApiTransformer
import com.erdeanmich.todings.web.service.ToDoService
import kotlinx.coroutines.launch
import java.util.*

class OverviewViewModel(application: Application) : AndroidViewModel(application) {
    private var currentSortMode = ToDoOverviewSortMode.PRIORITY_DATE
    private val transformer = ApiTransformer()
    private val service by lazy { ToDoService.getService() }
    private val toDoItemDao: ToDoItemDAO by lazy { DatabaseProvider.getToDoItemDAO(application) }
    private val apiErrorMessage = MutableLiveData<String>()
    private val apiSuccessMessage = MutableLiveData<String>()
    private val toDoItems: MutableLiveData<List<ToDoItem>> by lazy {
        MutableLiveData<List<ToDoItem>>().also {
            loadToDoItems()
        }
    }


    fun getToDoItems(): LiveData<List<ToDoItem>> {
        loadToDoItems()
        return toDoItems
    }

    fun getApiErrorMessages(): LiveData<String> {
        return apiErrorMessage
    }

    fun getApiSuccessMessages(): LiveData<String> {
        return apiSuccessMessage
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

    fun update(toDoItem: ToDoItem) {
        viewModelScope.launch {
            toDoItemDao.update(toDoItem)
            service.updateToDo(toDoItem.id, transformer.localToApi(toDoItem))
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
            c.set(Calendar.SECOND, 0)
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

    fun deleteRemoteToDos() {
        viewModelScope.launch {
            val response = service.deleteAllToDos()
            if (response.isSuccessful) {
                apiSuccessMessage.value = "Deleted all remote ToDings"
            } else {
                apiErrorMessage.value = "Deleting ToDings failed with: ${response.message()}"
            }
        }
    }

    fun getRemoteToDos() {
       viewModelScope.launch {
           val response = service.getAllToDos()
           if(!response.isSuccessful) {
               apiErrorMessage.value = "Getting ToDings failed with: ${response.message()}"
               return@launch
           }

           val items = response.body()?.map { transformer.apiToLocal(it) }?: emptyList<ToDoItem>()
           toDoItemDao.deleteAll()
           items.forEach { toDoItemDao.insert(it)}
           toDoItems.value = loadSortedToDoItems()
       }
    }

    fun syncLocalToDosToRemote() {
        viewModelScope.launch {
            val service = service
            val deleteResponse = service.deleteAllToDos()
            if (!deleteResponse.isSuccessful) {
                apiErrorMessage.value = "Deleting ToDings failed with: ${deleteResponse.message()}"
                return@launch
            }

            val items = toDoItems.value?: emptyList()
            val results = items.map { transformer.localToApi(it) }
                .map {
                    val insertResponse = service.createToDo(it)
                    insertResponse.isSuccessful
                }
                .groupingBy { it }
                .eachCount()

            val successful = results[true]
            apiSuccessMessage.value = "Synced $successful of ${items.size} ToDings to server"
        }
    }

}
