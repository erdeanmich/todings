package com.erdeanmich.todings.web.service

import com.erdeanmich.todings.web.model.ToDoApiModel
import retrofit2.Response
import retrofit2.http.*

interface ToDoService {
    @GET("todos")
    suspend fun getAllToDos(): Response<List<ToDoApiModel>>

    @POST("todos")
    suspend fun createToDo(@Body todo: ToDoApiModel): Response<Void>

    @GET("todos/{id}")
    suspend fun getToDo(@Path("id") id: Long): Response<Void>

    @PUT("todos/{id}")
    suspend fun updateToDo(@Path("id") id: Long, @Body todo: ToDoApiModel): Response<Void>

    @DELETE("todos")
    suspend fun deleteAllToDos(): Response<Boolean>

    @DELETE("todos/{id}")
    suspend fun deleteToDo(@Path("id") id: Long): Response<Boolean>

    companion object {
        private var toDoService: ToDoService? = null

        fun getService(): ToDoService {
            if (toDoService == null) {
                val retrofit = Retrofit.getRetrofit()
                toDoService = retrofit.create(ToDoService::class.java)
            }

            return toDoService!!
        }
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               