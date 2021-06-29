package com.erdeanmich.todings.web.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.243:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}