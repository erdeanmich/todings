package com.erdeanmich.todings.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun getToDoItemDAO(context: Context): ToDoItemDAO {
        synchronized(AppDatabase::class) {
            if (database == null) {
                database = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "todo-database"
                ).build()
            }
        }
        return database!!.toDoItemDao()
    }
}