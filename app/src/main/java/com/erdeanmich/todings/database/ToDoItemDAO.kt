package com.erdeanmich.todings.database

import androidx.room.*
import com.erdeanmich.todings.model.ToDoItem

@Dao
interface ToDoItemDAO {
    @Query("SELECT * FROM ToDoItem")
    suspend fun getAll(): List<ToDoItem>

    @Query("SELECT * FROM ToDoItem WHERE id = :id")
    suspend fun getById(id: Long): ToDoItem

    @Query("DELETE FROM ToDoItem")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(toDoItem: ToDoItem) : Long

    @Update
    suspend fun update(toDoItem: ToDoItem)

    @Delete
    suspend fun delete(toDoItem: ToDoItem)
}