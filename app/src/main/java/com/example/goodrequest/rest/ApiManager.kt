package com.example.goodrequest.rest

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {
    private var apiService: ApiService
    companion object {
        private const val BASE_URL = "https://reqres.in/api/"
        val instance = ApiManager().apiService
    }

    init {
        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}