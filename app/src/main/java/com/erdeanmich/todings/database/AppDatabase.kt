package com.erdeanmich.todings.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erdeanmich.todings.model.ToDoItem

@Database(entities = [ToDoItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toDoItemDao() : ToDoItemDAO
}