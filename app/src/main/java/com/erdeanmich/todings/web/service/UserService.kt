package com.erdeanmich.todings.web.service

import com.erdeanmich.todings.web.model.UserApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface UserService {
    @PUT("users/auth")
    suspend fun authenticate(@Body user: UserApiModel): Response<Boolean>

    companion object {
        private var userService: UserService? = null
        fun getService(): UserService {
            if (userService == null){
                val retrofit = Retrofit.getRetrofit()
                userService = retrofit.create(UserService::class.java)
            }

            return userService!!
        }
    }
}